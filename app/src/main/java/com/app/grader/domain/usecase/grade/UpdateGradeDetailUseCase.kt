package com.app.grader.domain.usecase.grade

import com.app.grader.domain.model.GradeDetailModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.usecase.subGrade.DeleteAllSubGradesFromGradeUseCase
import com.app.grader.domain.usecase.subGrade.SaveSubGradeUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UpdateGradeDetailUseCase @Inject constructor(
    private val updateGradeUseCase: UpdateGradeUseCase,
    private val deleteAllSubGradesFromGradeUseCase: DeleteAllSubGradesFromGradeUseCase,
    private val saveSubGradeUseCase: SaveSubGradeUseCase
) {
    operator fun invoke(gradeDetailModel: GradeDetailModel): Flow<Resource<Unit>> = channelFlow {
        try {
            send(Resource.Loading())
            val updateResult = updateGradeUseCase(gradeDetailModel).first { it !is Resource.Loading }
            when (updateResult) {
                is Resource.Success -> {
                    val deleteResult = deleteAllSubGradesFromGradeUseCase(gradeDetailModel.id)
                        .first { it !is Resource.Loading }
                    if (deleteResult is Resource.Error) {
                        send(Resource.Error(deleteResult.message ?: "Error al eliminar sub-calificaciones"))
                        return@channelFlow
                    }
                    for (subGrade in gradeDetailModel.subgrades) {
                        val subResult = saveSubGradeUseCase(subGrade.copy(gradeId = gradeDetailModel.id))
                            .first { it !is Resource.Loading }
                        if (subResult is Resource.Error) {
                            send(Resource.Error(subResult.message ?: "Error al guardar sub-calificación"))
                            return@channelFlow
                        }
                    }
                    send(Resource.Success(Unit))
                }
                is Resource.Error -> {
                    send(Resource.Error<Unit>(updateResult.message ?: "Error al actualizar calificación"))
                    return@channelFlow
                }
                else -> {}
            }
        } catch (e: Exception) {
            send(Resource.Error(e.message ?: "Unknown Error"))
        }
    }
}

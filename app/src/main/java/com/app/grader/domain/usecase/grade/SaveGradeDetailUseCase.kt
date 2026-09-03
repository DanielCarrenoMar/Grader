package com.app.grader.domain.usecase.grade

import com.app.grader.domain.model.GradeDetailModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.usecase.subGrade.SaveSubGradeUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SaveGradeDetailUseCase @Inject constructor(
    private val saveGradeUseCase: SaveGradeUseCase,
    private val saveSubGradeUseCase: SaveSubGradeUseCase
) {
    operator fun invoke(gradeDetailModel: GradeDetailModel): Flow<Resource<Unit>> = channelFlow {
        try {
            send(Resource.Loading())
            val result = saveGradeUseCase(gradeDetailModel).first { it !is Resource.Loading }
            when (result) {
                is Resource.Success -> {
                    val gradeId = result.data?.toInt() ?: -1
                    if (gradeId == -1) {
                        send(Resource.Error<Unit>("Error al guardar calificación"))
                        return@channelFlow
                    }
                    for (subGrade in gradeDetailModel.subgrades) {
                        val subResult = saveSubGradeUseCase(subGrade.copy(gradeId = gradeId))
                            .first { it !is Resource.Loading }
                        if (subResult is Resource.Error) {
                            send(Resource.Error<Unit>(subResult.message ?: "Error al guardar sub-calificación"))
                            return@channelFlow
                        }
                    }
                    send(Resource.Success(Unit))
                }
                is Resource.Error -> {
                    send(Resource.Error<Unit>(result.message ?: "Error al guardar calificación"))
                    return@channelFlow
                }
                else -> {}
            }
        } catch (e: Exception) {
            send(Resource.Error(e.message ?: "Unknown Error"))
        }
    }
}

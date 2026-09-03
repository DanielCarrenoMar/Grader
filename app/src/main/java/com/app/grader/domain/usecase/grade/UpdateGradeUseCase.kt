package com.app.grader.domain.usecase.grade

import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.policy.GradeRules
import com.app.grader.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class UpdateGradeUseCase @Inject constructor(
    private val repository: LocalStorageRepository,
    private val gradeRules: GradeRules
) {
    operator fun invoke(gradeModel: GradeModel): Flow<Resource<Unit>> = channelFlow {
        try {
            send(Resource.Loading())
            gradeModel.validate()
            val currentSumWithoutThis = repository.getGradesFromCourse(gradeModel.courseId)
                .filter { it.id != gradeModel.id }
                .sumOf { it.percentage.getPercentage() }
            gradeRules.validateSumNotExceed100(currentSumWithoutThis, gradeModel.percentage.getPercentage())
            if (repository.updateGrade(gradeModel)){
                send(
                    Resource.Success(data = Unit)
                )
            } else {
                send(
                    Resource.Error("Update grade Error")
                )
            }
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}
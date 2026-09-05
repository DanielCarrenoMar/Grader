package com.app.grader.domain.usecase.grade

import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.policy.GradeRules
import com.app.grader.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class SaveGradeUseCase @Inject constructor(
    private val repository: LocalStorageRepository,
    private val gradeRules: GradeRules
) {
    operator fun invoke(gradeModel: GradeModel): Flow<Resource<Long>> = channelFlow {
        try {
            send(Resource.Loading())
            gradeModel.validate()
            val currentSum = repository.getGradesFromCourse(gradeModel.courseId)
                .sumOf { it.weight.getPercentage() }
            gradeRules.validateSumNotExceed100(currentSum, gradeModel.weight.getPercentage())
            val data = repository.saveGrade(gradeModel)
            if (data.toInt() != -1){
                send(
                    Resource.Success(data = data)
                )
            } else {
                send(
                    Resource.Error("Save grade Error")
                )
            }
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}
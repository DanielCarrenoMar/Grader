package com.app.grader.ui.pages.editGrade

import com.app.grader.domain.types.Grade
import com.app.grader.domain.usecase.course.*
import com.app.grader.domain.usecase.grade.*
import com.app.grader.domain.usecase.subGrade.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

object EditGradeViewModelMother {
    fun create(): EditGradeViewModel {
        return EditGradeViewModel(
            mock(GetGradeFromIdUseCase::class.java),
            mock(GetGradesFromCourseUseCase::class.java),
            mock(SaveGradeUseCase::class.java),
            mock(UpdateGradeUseCase::class.java),
            mock(GetAllCoursesUserCase::class.java),
            mock(GetCourseFromIdUseCase::class.java),
            mock(GetSubGradesFromGradeUseCase::class.java),
            mock(SaveSubGradeUseCase::class.java),
            mock(DeleteAllSubGradesFromGradeIdUseCase::class.java)
        )
    }
}

class ValidateInputsModelTest {
    private lateinit var viewModel: EditGradeViewModel

    @Before
    fun setUp() {
        viewModel = EditGradeViewModelMother.create()
    }

    @Test
    fun `syncInvalidInputs retorna true con datos por defecto`() {
        val result = viewModel.syncInvalidInputs()
        assertTrue(result)
    }

    @Test
    fun `syncInvalidInputs retorna true con solo porcentaje`() {
        viewModel.setPercentage("50")
        val result = viewModel.syncInvalidInputs()
        assertTrue(result)
    }
    @Test
    fun `syncInvalidInputs retorna true con solo la calificación`() {
        viewModel.setGrade("15")
        val result = viewModel.syncInvalidInputs()
        assertTrue(result)
    }

    @Test
    fun `syncInvalidInputs retorna false con grade es mas de 20`() {
        viewModel.setGrade("40")
        val result = viewModel.syncInvalidInputs()
        assertFalse(result)
    }

    @Test
    fun `syncInvalidInputs retorna false con grade es negativo`() {
        viewModel.setGrade("-10")
        val result = viewModel.syncInvalidInputs()
        assertFalse(result)
    }

    @Test
    fun `syncInvalidInputs retorna false con grade en valor Blank`() {
        val value = Grade().toString()
        viewModel.setGrade(value)
        val result = viewModel.syncInvalidInputs()
        assertFalse(result)
    }

    @Test
    fun `syncInvalidInputs retorna false si percentage es negativo`() {
        viewModel.setPercentage("-10")
        val result = viewModel.syncInvalidInputs()
        assertFalse(result)
    }

    @Test
    fun `syncInvalidInputs retorna false si percentage es mas de 100`() {
        viewModel.setPercentage("101")
        val result = viewModel.syncInvalidInputs()
        assertFalse(result)
    }

    @Test
    fun `syncInvalidInputs pone valores por defecto si title o description están vacíos`() {
        viewModel.setGrade("15")
        viewModel.setPercentage("50")
        viewModel.showTitle.value = ""
        viewModel.showDescription.value = ""
        viewModel.syncInvalidInputs()
        assertEquals("Sin Titulo", viewModel.showTitle.value)
        assertEquals("Sin descripción", viewModel.showDescription.value)
    }
}

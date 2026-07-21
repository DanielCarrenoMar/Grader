package com.app.grader.ui.componets.chart

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.VicoZoomState
import com.patrykandpatrick.vico.compose.cartesian.Zoom
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.lineSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent

@Composable
fun LineChartAverage(gradeSeries: List<Double>, modifier: Modifier = Modifier) {
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(gradeSeries) {
        modelProducer.runTransaction {
            lineSeries { series(gradeSeries) }
        }
    }
    JetpackComposeBasicLineChart(modelProducer, modifier)
}

@Composable
private fun JetpackComposeBasicLineChart(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier,
) {
    val lineColor = MaterialTheme.colorScheme.secondary
    CartesianChartHost(
        chart =
            rememberCartesianChart(
                rememberLineCartesianLayer(
                    LineCartesianLayer.LineProvider.series(
                        LineCartesianLayer.rememberLine(
                            fill = LineCartesianLayer.LineFill.single(Fill(lineColor)),
                            areaFill = LineCartesianLayer.AreaFill.single(
                                Fill(
                                    Brush.verticalGradient(
                                        listOf(
                                            lineColor.copy(alpha = 0.4f),
                                            Color.Transparent,
                                        )
                                    )
                                )
                            ),
                            pointProvider = LineCartesianLayer.PointProvider.single(
                                LineCartesianLayer.Point(
                                    rememberShapeComponent(
                                        Fill(lineColor),
                                        shape = RoundedCornerShape(50)
                                    )
                                )
                            ),
                            pointConnector = LineCartesianLayer.PointConnector.cubic(),
                        )
                    )
                )
            ),
        modelProducer = modelProducer,
        modifier = modifier,
        zoomState = VicoZoomState(
            zoomEnabled = false,
            initialZoom = Zoom.Content,
            minZoom = Zoom.fixed(0.1f),
            maxZoom = Zoom.fixed(1f)
        )
    )
}
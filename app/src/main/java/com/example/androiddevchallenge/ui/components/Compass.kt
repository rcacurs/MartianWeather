/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge.ui.components

import android.graphics.Paint
import android.graphics.Rect
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.theme.deepOrangeLight

@Composable
fun Compass(
    modifier: Modifier,
    color: Color = Color.White,
    angle: Float = 0f,
    nudge: MutableState<Boolean>
) {
    val needleAngle = animateFloatAsState(
        targetValue = angle + if (nudge.value) 45f else 0f,
        animationSpec =
        if (nudge.value) {
            tween(10)
        } else {
            spring(
                Spring.DampingRatioHighBouncy,
                stiffness = Spring.StiffnessLow
            )
        },
        finishedListener = {
            if (nudge.value) {
                nudge.value = false
            }
        }
    )

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.width
        val centerX = size.width / 2
        val centerY = size.width / 2
        val circleThickness = canvasWidth / 25
        val compassRadius = (size.minDimension / 2) - (circleThickness / 2)
        val needleLineThickness = circleThickness / 4
        val labelSize = compassRadius / 2.5f
        drawCircle(
            color = color,
            center = Offset(x = canvasWidth / 2, y = canvasHeight / 2),
            radius = compassRadius,
            style = Stroke(circleThickness)
        )
        val compassUp = Path()
        compassUp.moveTo(centerX, centerY - needleLineThickness / 2)
        compassUp.lineTo(centerX + compassRadius / 5, centerY - needleLineThickness / 2)
        compassUp.lineTo(centerX, centerY - compassRadius / 2 - needleLineThickness / 2)
        compassUp.lineTo(centerX - compassRadius / 5, centerY - needleLineThickness / 2)
        compassUp.lineTo(centerX, centerY - needleLineThickness / 2)

        val compassDown = Path()
        compassDown.moveTo(centerX, centerY + needleLineThickness / 2)
        compassDown.lineTo(centerX + compassRadius / 5, centerY + needleLineThickness / 2)
        compassDown.lineTo(centerX, centerY + compassRadius / 2 + needleLineThickness / 2)
        compassDown.lineTo(centerX - compassRadius / 5, centerY + needleLineThickness / 2)
        compassDown.lineTo(centerX, centerY + needleLineThickness / 2)
        rotate(
            degrees = needleAngle.value,
            pivot = Offset(centerX, centerY)
        ) {
            drawPath(
                path = compassUp,
                color = color,
                style = Stroke(needleLineThickness)
            )
            drawPath(
                path = compassUp,
                color = color,
            )
            drawPath(
                path = compassDown,
                color = color,
                style = Stroke(needleLineThickness)
            )
        }

        val paint = Paint()
        paint.textSize = labelSize
        paint.color = deepOrangeLight.toArgb()
        paint.textAlign = Paint.Align.CENTER
        val boundsE = Rect()
        paint.getTextBounds("E", 0, 1, boundsE)
        val boundsN = Rect()
        paint.getTextBounds("N", 0, 1, boundsN)
        val boundsS = Rect()
        paint.getTextBounds("S", 0, 1, boundsS)
        val boundsW = Rect()
        paint.getTextBounds("W", 0, 1, boundsW)

        drawContext.canvas.nativeCanvas.drawText(
            "N",
            centerX,
            centerY - compassRadius - boundsN.top + circleThickness,
            paint
        )
        drawContext.canvas.nativeCanvas.drawText(
            "E",
            centerX + compassRadius - boundsE.right / 2 - circleThickness,
            centerY - boundsE.top / 2,
            paint
        )
        drawContext.canvas.nativeCanvas.drawText(
            "S",
            centerX,
            centerY + compassRadius - circleThickness,
            paint
        )

        drawContext.canvas.nativeCanvas.drawText(
            "W",
            centerX - compassRadius + boundsW.right / 2 + circleThickness,
            centerY - boundsE.top / 2,
            paint
        )
    }
}

@Preview("Compass", widthDp = 400, heightDp = 700)
@Composable
fun PreviewCompass() {
    Compass(
        color = deepOrangeLight,
        modifier = Modifier.width(200.dp).height(200.dp),
        angle = 90f,
        nudge = mutableStateOf(false)
    )
}

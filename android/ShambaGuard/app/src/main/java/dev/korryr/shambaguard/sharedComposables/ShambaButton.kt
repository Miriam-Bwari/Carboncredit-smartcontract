package dev.korryr.shambaguard.sharedComposables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class ShambaButtonType {
    Filled,
    Text,
    Outlined,
    Elevated
}

@Composable
fun ShambaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    type: ShambaButtonType = ShambaButtonType.Filled,
    shape: Shape = RoundedCornerShape(16.dp),
    colors: ButtonColors? = null,
    elevation: ButtonElevation? = null,
    textStyle: TextStyle? = null,
    textColor: Color = Color.Unspecified
) {
    val defaultTextStyle = MaterialTheme.typography.labelLarge.copy(
        fontWeight = FontWeight.Bold
    )

    when (type) {
        ShambaButtonType.Filled -> {
            val defaultFilledColors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
            )
            
            val defaultElevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 4.dp
            )

            Button(
                onClick = onClick,
                enabled = enabled,
                modifier = modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = shape,
                colors = colors ?: defaultFilledColors,
                elevation = elevation ?: defaultElevation,
            ) {
                Text(
                    text = text,
                    style = textStyle ?: defaultTextStyle,
                    color = textColor
                )
            }
        }
        ShambaButtonType.Text -> {
            val defaultTextColors = ButtonDefaults.textButtonColors(
                contentColor = if (textColor != Color.Unspecified) textColor else MaterialTheme.colorScheme.primary,
                disabledContentColor = (if (textColor != Color.Unspecified) textColor else MaterialTheme.colorScheme.primary).copy(alpha = 0.5f)
            )

            TextButton(
                onClick = onClick,
                enabled = enabled,
                modifier = modifier,
                shape = shape,
                colors = colors ?: defaultTextColors,
                elevation = elevation,
            ) {
                Text(
                    text = text,
                    style = textStyle ?: defaultTextStyle,
                    color = textColor
                )
            }
        }
        ShambaButtonType.Outlined -> {
            val defaultOutlinedColors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (textColor != Color.Unspecified) textColor else MaterialTheme.colorScheme.primary,
                disabledContentColor = (if (textColor != Color.Unspecified) textColor else MaterialTheme.colorScheme.primary).copy(alpha = 0.5f)
            )

            OutlinedButton(
                onClick = onClick,
                enabled = enabled,
                modifier = modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = shape,
                colors = colors ?: defaultOutlinedColors,
                elevation = elevation,
            ) {
                Text(
                    text = text,
                    style = textStyle ?: defaultTextStyle,
                    color = textColor
                )
            }
        }
        ShambaButtonType.Elevated -> {
            val defaultElevatedColors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = if (textColor != Color.Unspecified) textColor else MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                disabledContentColor = (if (textColor != Color.Unspecified) textColor else MaterialTheme.colorScheme.primary).copy(alpha = 0.5f)
            )

            val defaultElevatedElevation = ButtonDefaults.elevatedButtonElevation(
                defaultElevation = 1.dp,
                pressedElevation = 2.dp
            )

            ElevatedButton(
                onClick = onClick,
                enabled = enabled,
                modifier = modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = shape,
                colors = colors ?: defaultElevatedColors,
                elevation = elevation ?: defaultElevatedElevation,
            ) {
                Text(
                    text = text,
                    style = textStyle ?: defaultTextStyle,
                    color = textColor
                )
            }
        }
    }
}

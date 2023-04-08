package com.zacharee1.systemuituner.compose.preferences

import android.annotation.ColorRes
import android.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.zacharee1.systemuituner.compose.components.SeekBar
import com.zacharee1.systemuituner.compose.rememberBooleanSettingsState
import com.zacharee1.systemuituner.compose.rememberFloatSettingsState
import com.zacharee1.systemuituner.compose.rememberIntSettingsState
import com.zacharee1.systemuituner.compose.rememberSettingsState
import com.zacharee1.systemuituner.data.SettingsType
import com.zacharee1.systemuituner.util.SettingsInfo
import com.zacharee1.systemuituner.util.getSetting

open class ListPreferenceItem(
    override val title: String,
    override val key: String,
    override val minApi: Int = -1,
    override val maxApi: Int = Int.MAX_VALUE,
    override val enabled: @Composable () -> Boolean = { true },
    @ColorRes override val iconColor: Int? = null,
    override val dangerous: Boolean = false,
    override val summary: String? = null,
    @DrawableRes override val icon: Int? = null,
    override val saveOption: Boolean = true,
    override val revertable: Boolean = false,
    val options: Array<Option>,
    val defaultOption: Any?,
    val settingsType: SettingsType,
    val writeKey: String,
    val testing: Boolean = false,
) : SettingsPreferenceItem(
    title = title, key = key,
    minApi = minApi, maxApi = maxApi,
    enabled = enabled, iconColor = iconColor,
    dangerous = dangerous, summary = summary,
    icon = icon, saveOption = saveOption,
    revertable = revertable,
    dialogContents = {
        val context = LocalContext.current
        var state by if (testing) {
            remember {
                mutableStateOf(defaultOption?.toString())
            }
        } else {
            context.rememberSettingsState(
                key = settingsType to writeKey,
                value = {
                    context.getSetting(settingsType, writeKey, defaultOption)
                },
                revertable = revertable,
                saveOption = saveOption,
            )
        }
        
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = options, { it.hashCode() }) { item ->
                val selected = state == item.value?.toString()
                val cardBackground by animateColorAsState(
                    targetValue = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f) else Color.Transparent,
                    label = "Card color $item"
                )

                OutlinedCard(
                    modifier = Modifier.selectable(
                        selected = selected
                    ) {
                        state = item.value?.toString()
                    },
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = cardBackground
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = item.label)
                    }
                }
            }
        }
    }
) {
    data class Option(
        val label: String,
        val value: Any?,
    )
}

open class SeekBarPreferenceItem(
    override val title: String,
    override val key: String,
    override val minApi: Int = -1,
    override val maxApi: Int = Int.MAX_VALUE,
    override val enabled: @Composable () -> Boolean = { true },
    @ColorRes override val iconColor: Int? = null,
    override val dangerous: Boolean = false,
    override val summary: String? = null,
    @DrawableRes override val icon: Int? = null,
    override val saveOption: Boolean = true,
    override val revertable: Boolean = false,
    val writeKey: Pair<SettingsType, String>,
    val minValue: Number,
    val maxValue: Number,
    val defaultValue: Number,
    val unit: String,
    val scale: Double = 1.0,
    val testing: Boolean = false,
) : SettingsPreferenceItem(
    title = title, key = key,
    minApi = minApi, maxApi = maxApi,
    enabled = enabled, iconColor = iconColor,
    dangerous = dangerous, summary = summary,
    icon = icon, saveOption = saveOption,
    revertable = revertable,
    dialogContents = {
        val context = LocalContext.current
        var state by if (testing) {
            remember {
                mutableStateOf(defaultValue)
            }
        } else {
            if (scale == 1.0) {
                context.rememberIntSettingsState(
                    key = writeKey,
                    saveOption = saveOption,
                    revertable = revertable,
                    def = defaultValue.toInt()
                )
            } else {
                context.rememberFloatSettingsState(
                    key = writeKey,
                    saveOption = saveOption,
                    revertable = revertable,
                    def = defaultValue.toFloat()
                )
            }
        }

        OutlinedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            SeekBar(
                minValue = minValue,
                maxValue = maxValue,
                defaultValue = defaultValue,
                scale = scale,
                value = state,
                onValueChanged = { state = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
)

@OptIn(ExperimentalMaterial3Api::class)
open class SwitchPreferenceItem(
    override val title: String,
    override val key: String,
    override val minApi: Int = -1,
    override val maxApi: Int = Int.MAX_VALUE,
    override val enabled: @Composable () -> Boolean = { true },
    @ColorRes override val iconColor: Int? = null,
    override val dangerous: Boolean = false,
    override val summary: String? = null,
    @DrawableRes override val icon: Int? = null,
    override val saveOption: Boolean = true,
    override val revertable: Boolean = false,
    val writeKeys: Array<Pair<SettingsType, String>>,
    val enabledValue: Any? = 1,
    val disabledValue: Any? = 0,
): SettingsPreferenceItem(
    title, key, minApi, maxApi, enabled, iconColor,
    dangerous, summary, icon, saveOption = saveOption,
    revertable = revertable, dialogContents = {
        val context = LocalContext.current
        var state by context.rememberBooleanSettingsState(
            keys = writeKeys,
            enabledValue = enabledValue,
            disabledValue = disabledValue,
            saveOption = saveOption,
            revertable = revertable,
        )

        OutlinedCard(
            onClick = { state = !state }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.size(8.dp))

                Switch(
                    checked = state,
                    onCheckedChange = { state = it }
                )
            }
        }
    }
)

open class SettingsPreferenceItem(
    override val title: String,
    override val key: String,
    override val minApi: Int = -1,
    override val maxApi: Int = Int.MAX_VALUE,
    override val enabled: @Composable () -> Boolean = { true },
    @ColorRes override val iconColor: Int? = null,
    override val dangerous: Boolean = false,
    override val summary: String? = null,
    @DrawableRes override val icon: Int? = null,
    open val dialogContents: @Composable ColumnScope.(saveCallback: (Array<SettingsInfo>) -> Unit) -> Unit,
    open val saveOption: Boolean = true,
    open val revertable: Boolean = false,
) : BasePreferenceItem(
    title, key, minApi, maxApi, enabled, iconColor,
    dangerous, summary, icon
)

open class PreferenceItem(
    override val title: String,
    override val key: String,
    override val minApi: Int = -1,
    override val maxApi: Int = Int.MAX_VALUE,
    override val enabled: @Composable () -> Boolean = { true },
    @ColorRes override val iconColor: Int? = null,
    override val dangerous: Boolean = false,
    override val summary: String? = null,
    @DrawableRes override val icon: Int? = null,
    open val onClick: (() -> Unit)? = null,
) : BasePreferenceItem(
    title, key, minApi, maxApi, enabled, iconColor,
    dangerous, summary, icon
)

open class BasePreferenceItem(
    open val title: String,
    open val key: String,
    open val minApi: Int = -1,
    open val maxApi: Int = Int.MAX_VALUE,
    open val enabled: @Composable () -> Boolean = { true },
    @ColorRes open val iconColor: Int? = null,
    open val dangerous: Boolean = false,
    open val summary: String? = null,
    @DrawableRes open val icon: Int? = null,
)

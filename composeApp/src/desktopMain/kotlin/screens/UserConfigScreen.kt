package screens

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import screens.model.UserConfigScreenModel

typealias OnTextChange = (String) -> Unit

class UserConfigScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<UserConfigScreenModel>()
        val state by screenModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        val onPressSaveButton = {
            screenModel.save()
            navigator.pop()
        }

        ContentState(
            username = state.username,
            javaPath = state.javaPath,
            onChangeUsername = { screenModel.updateUsername(it) },
            onChangeJavaPath = { screenModel.updateJavaPath(it) },
            onPressSaveButton = { onPressSaveButton() },
        )
    }

    @Composable
    fun ContentState(
        username: String = "",
        javaPath: String = "Java",
        onChangeUsername: OnTextChange = {},
        onChangeJavaPath: OnTextChange = {},
        onPressSaveButton: () -> Unit = {},
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(top = 32.dp),
        ) {
            Text(
                "Configurações",
                fontSize = 24.sp,
            )
            Spacer(modifier = Modifier.padding(horizontal = 24.dp))
            TextField(title = "Usuário", value = username, onChange = onChangeUsername)
            TextField(title = "Java", value = javaPath, onChange = onChangeJavaPath)
            Spacer(modifier = Modifier.padding(horizontal = 16.dp))
            TextButton(onClick = onPressSaveButton) {
                Text("Salvar")
            }
        }
    }

    @Composable
    private fun TextField(
        title: String,
        value: String,
        onChange: OnTextChange,
    ) {
        val titleComposable = @Composable { Text(title) }
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            placeholder = titleComposable,
            label = titleComposable,
        )
    }
}

@Preview
@Composable
fun Preview() {
    UserConfigScreen().ContentState()
}

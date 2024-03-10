package com.example.demo.ui.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.example.demo.data.Response
import com.example.demo.model.Element
import com.example.demo.model.ElementType
import com.example.demo.ui.theme.DemoTheme
import com.example.demo.ui.theme.Spacing
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainComposable(viewModel)
                }
            }
        }
    }
}

@Composable
fun MainComposable(viewModel: MainActivityViewModel) {
    // Collect the StateFlow as state
    val uiState: UiState<Response> by viewModel.state.collectAsState()
    when (val state = uiState) {
        is UiState.Error -> {
            ErrorText(state.exception)
        }
        UiState.Loading -> {
            LinearProgressIndicator()
        }
        is UiState.Success -> {
            ArticleList(state.data)
        }
    }
}

@Composable
fun ArticleCard(elements: List<Element>) {
    Card(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(Spacing.Medium)) {
            elements.forEach { element ->
                ElementFactory(element = element)
            }
        }
    }
}

@Composable
fun ArticleList(data: Response) {
    val elements = data.items.map { it.elements }
    LazyColumn(
        contentPadding = PaddingValues(Spacing.Large),
        verticalArrangement = Arrangement.spacedBy(Spacing.Small)
    ) {
        items(elements) {
            ArticleCard(it)
        }
    }
}

@Composable
fun ErrorText(exception: Exception) {
    Text(
        text = "There was an error!! : ${exception.message}",
        style = MaterialTheme.typography.h2
    )
}

@Composable
fun ElementFactory(element: Element) {
    when (element) {
        is Element.TextElement -> {
            TextElement(element)
        }
        is Element.ButtonElement -> {
            OutlinedButton(onClick = { }, modifier = Modifier.fillMaxWidth()) {
                Text(element.text)
            }
        }
        is Element.ImageElement -> {
            // not implemented
        }
    }
}

@Composable
fun TextElement(element: Element.TextElement) {
    when (element.purpose) {
        ElementType.TextType.TITLE.value -> {
            Text(
                text = element.text,
                style = MaterialTheme.typography.h3,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        ElementType.TextType.DESCRIPTION.value -> {
            Text(
                text = element.text,
                style = MaterialTheme.typography.body1,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

package br.com.fiap.seletordeequipes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fiap.seletordeequipes.ui.theme.SeletorDeEquipesTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SeletorDeEquipesTheme {
                SeletorDeEquipes()
            }
        }
    }
}

@Composable
fun SeletorDeEquipes() {
    var nomes by remember { mutableStateOf(listOf<String>()) }
    var novoNome by remember { mutableStateOf("") }
    var nomeEditavel by remember { mutableStateOf("") }
    var indiceEditavel by remember { mutableIntStateOf(-1) }
    var numEquipes by remember { mutableStateOf("") }
    var equipes by remember { mutableStateOf(listOf<List<String>>()) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Campo para adicionar nome
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = novoNome,
                onValueChange = { novoNome = it },
                label = { Text("Nome") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            IconButton(onClick = {
                if (novoNome.isNotBlank()) {
                    nomes = nomes + novoNome
                    novoNome = ""
                }
            }) {
                Icon(imageVector = Icons.Filled.AddCircle, contentDescription = "Adicionar Nome")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de nomes com ícones
        LazyColumn {
            itemsIndexed(nomes) { index, nome ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (indiceEditavel == index) {
                        OutlinedTextField(
                            value = nomeEditavel,
                            onValueChange = { nomeEditavel = it },
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            nomes = nomes.mapIndexed { i, n -> if (i == index) nomeEditavel else n }
                            indiceEditavel = -1
                            nomeEditavel = ""
                        }) {
                            Icon(imageVector = Icons.Filled.Check, contentDescription = "Salvar")
                        }
                    } else {
                        Text(nome, modifier = Modifier.weight(1f))
                    }

                    if (indiceEditavel != index) { // Show edit icon only when not in edit mode
                        IconButton(onClick = {
                            nomeEditavel = nome
                            indiceEditavel = index
                        }) {
                            Icon(imageVector = Icons.Filled.Edit, contentDescription = "Editar")
                        }
                    }

                    IconButton(onClick = {
                        nomes = nomes.filterIndexed { i, _ -> i != index }
                        if (indiceEditavel == index) {
                            indiceEditavel = -1 // Reset the editable index if the item is deleted
                            nomeEditavel = ""
                        }
                    }) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Remover")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo para número de equipes
        OutlinedTextField(
            value = numEquipes,
            onValueChange = { numEquipes = it },
            label = { Text("Número de Equipes") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botões
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = {
                if (numEquipes.isNotBlank()) {
                    val num = numEquipes.toInt()
                    equipes = dividirEmEquipes(nomes, num)
                }
            }) {
                Text("Dividir em Equipes")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(onClick = {
                nomes = emptyList()
                numEquipes = ""
                equipes = emptyList()
            }) {
                Text("Limpar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar equipes
        if (equipes.isNotEmpty()) {
            LazyColumn {
                items(equipes) { equipe ->
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Equipe:")
                        equipe.forEach { nome ->
                            Text("- $nome")
                        }
                    }
                }
            }
        }
    }
}

fun dividirEmEquipes(nomes: List<String>, numEquipes: Int): List<List<String>> {
    if (numEquipes <= 0 || nomes.isEmpty()) {
        return emptyList()
    }

    val random = Random(System.currentTimeMillis())
    val nomesEmbaralhados = nomes.shuffled(random)
    val equipes = MutableList(numEquipes) { mutableListOf<String>() }

    for ((index, nome) in nomesEmbaralhados.withIndex()) {
        val indiceEquipe = index % numEquipes
        equipes[indiceEquipe].add(nome)
    }
    return equipes
}

@Preview
@Composable
fun SeletorDeEquipesPreview() {
    SeletorDeEquipes()
}
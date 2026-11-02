# Atividade_1_Sem - Vou de Bus

Aplicativo Android desenvolvido como atividade prática da matéria de
**Programação para Disp. Móveis I**. O app permite consultar linhas de transporte
público, filtrar os trajetos por origem e destino e visualizar no mapa o
percurso selecionado, com localização do usuário e acompanhamento da posição
simulada do ônibus.

<div align="center">
<img src=".github\project-image.png" alt="Vou de Bus"/><br />
</div>

## Funcionalidades

- Listagem das linhas de transporte disponíveis;
- Filtro de linhas por local de origem e destino;
- Exibição dos horários de início e fim e do intervalo entre os ônibus;
- Visualização do trajeto em um mapa;
- Marcadores para a origem, o destino e o ônibus;
- Exibição da localização atual do usuário, mediante permissão;
- Atualização da posição do ônibus ao longo do percurso;
- Suporte a temas claro e escuro.

## Ferramentas utilizadas

- **[Android Studio](https://developer.android.com/studio)**;
- **[Java](https://www.java.com/)**;
- **[Gradle](https://gradle.org/)**;
- **[Android SDK](https://developer.android.com/studio)**;
- **[AndroidX AppCompat](https://developer.android.com/jetpack/androidx/releases/appcompat)**;
- **[Material Components](https://material.io/develop/android)**;
- **[ConstraintLayout](https://developer.android.com/develop/ui/views/layout/constraint-layout)**;
- **[Navigation Component](https://developer.android.com/guide/navigation)**;
- **[osmdroid](https://github.com/osmdroid/osmdroid)**, para exibição do mapa;
- **[OSRM](https://project-osrm.org/)**, para obter a rota entre os pontos.

As versões e dependências do projeto estão definidas em
[libs.versions.toml](gradle/libs.versions.toml) e
[app/build.gradle.kts](app/build.gradle.kts).

## Requisitos

Para abrir e executar o projeto, é necessário ter:

- Android Studio instalado;
- Android SDK com a API 36 disponível;
- JDK 11 ou compatível com a configuração do projeto;
- Um dispositivo Android ou emulador com Android 7.0 (API 24) ou superior;
- Acesso à internet para carregar os mapas e consultar as rotas;
- Permissão de localização, caso seja utilizada a função de localização do
  usuário.

## Estrutura dos dados

As linhas de transporte são armazenadas inicialmente no arquivo
[lines.json](app/src/main/res/raw/lines.json). Na primeira execução, esse
conteúdo é copiado para o `SharedPreferences` do aplicativo. Cada linha
contém:

- origem e destino;
- horário de início e término;
- intervalo entre os ônibus;
- coordenadas geográficas de origem e destino.

Não é necessário configurar variáveis de ambiente ou um banco de dados externo
para executar o projeto.

## Rodando o projeto (desenvolvimento)

```bash
# Clone este repositório

$ git clone <URL_DO_REPOSITORIO>

# Acesse a pasta do projeto no terminal/cmd

$ cd Atividade_1_SEM

# Abra a pasta do projeto no Android Studio

# Aguarde a sincronização do Gradle e a instalação das dependências

# Conecte um dispositivo Android ou inicie um emulador

# Execute a aplicação pelo botão Run do Android Studio
```

Também é possível executar pelo terminal, com um dispositivo ou emulador
conectado:

```bash
./gradlew assembleDebug
./gradlew installDebug
```

No Windows, utilize `gradlew.bat` no lugar de `./gradlew`.

## Permissões

O aplicativo solicita as permissões de internet, estado da rede e localização
aproximada ou precisa. A permissão de localização é necessária apenas para
centralizar o mapa na posição atual do usuário; os trajetos cadastrados podem
ser consultados sem informar a localização.

## Navegação do aplicativo

1. Na tela inicial, informe opcionalmente os locais nos campos **De onde** e
   **Para onde**;
2. Selecione uma linha na lista filtrada;
3. Na tela do mapa, consulte o percurso, os marcadores e o horário estimado;
4. Use o botão de localização para centralizar o mapa na posição atual;
5. Use o botão de cancelar para retornar à lista de linhas.

## TODO's

- Substituir os dados estáticos por dados reais de transporte público;
- Permitir atualização das linhas sem precisar atualizar o aplicativo;
- Melhorar o tratamento de erros quando não houver conexão com a internet;
- Adicionar testes automatizados para filtros, carregamento dos dados e
  navegação.

## Desenvolvedores

<table align="center">
<tr>
<td>
<div align="center">
<img style="width: 150px; border-radius: 50%;" src="https://github.com/CaduZulian.png" alt="CaduZulian"/><br />
<a href="https://github.com/CaduZulian">Carlos Eduardo Zulian</a>
</div>
</td>
</tr>
</table>

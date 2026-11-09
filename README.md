# KzzKaiii

Launcher pessoal para Subway Surfers WebGL Reborn, derivado do **Volt Maps** e adaptado para uma experiência mobile.

> **Créditos:** o projeto-base é do Volt. Esta cópia preserva a atribuição ao projeto original. Os arquivos do jogo pertencem aos respectivos titulares e não são redistribuídos por este repositório.

## O que já está no repositório

- Launcher reidentificado como **KzzKaiii**, com interface dark glass e layout responsivo.
- Galeria, pesquisa, filtros e favoritos herdados da base.
- Seleção de modos **Enhanced / Original**, **Training** e **Pogo** quando o conteúdo correspondente está disponível.
- Timer integrado em `telechargement/tro-timer.js`, com automação por eventos do jogo, pausas, splits, atalhos, redimensionamento e configurações.
- Manifest e ícone próprios para a experiência web instalável.
- Projeto Android nativo em `android-app/`, com WebView acelerada e servidor Python local via Chaquopy.
- Workflow de GitHub Actions para compilar um APK Android de depuração.

## Android

O app foi preparado para aparelhos Android ARM64, incluindo Android 15.

O aplicativo copia o launcher para o armazenamento privado, inicia o servidor local na porta 8907 e abre a interface em `http://localhost:8907/`. Os arquivos solicitados pelo jogo podem ser armazenados no cache privado do aplicativo.

### Gerar o APK

O workflow **Build KzzKaiii APK** compila automaticamente quando há alterações relevantes na branch `master`.

1. Abra a aba [Actions](https://github.com/frisk001101100/kzzkaiii/actions).
2. Entre na execução mais recente de **Build KzzKaiii APK**.
3. Quando terminar com sucesso, baixe o artefato `KzzKaiii-android-debug`.
4. Extraia o ZIP e instale o APK no aparelho.

Para compilar localmente, use Java 17, Python 3.13 e Gradle 8.13:

```bash
cd android-app
gradle wrapper --gradle-version 8.13
./gradlew assembleDebug
```

O APK de depuração fica em `android-app/app/build/outputs/apk/debug/app-debug.apk`.

## Limites atuais — leia antes de instalar

- **O APK não inclui os builds Unity completos.** Eles são grandes e não estão no histórico Git deste fork; o servidor tenta obtê-los quando necessário.
- **A primeira execução exige internet.** O cache guarda arquivos já solicitados, mas isso não equivale a um pacote offline completo.
- **O fluxo de ativação ainda depende do serviço online.** Portanto, não prometemos que uma partida nova iniciará sem conexão, mesmo que parte dos arquivos já esteja em cache.
- **Training:** o launcher usa a rota Training do player principal. Os builds Training especiais do repositório privado citado na documentação original não estão incluídos aqui.
- **Pogo:** as páginas e configurações estão na base, mas os builds externos correspondentes não estão completos neste fork. O modo só funcionará onde os arquivos necessários estiverem disponíveis e seu uso for autorizado.
- O APK ainda precisa de teste em aparelho físico. O workflow de compilação não substitui o teste real de WebGL, timer, rotação e desempenho.

## Execução web local

A base Python usa:

```bash
python3 serve_localhost.py 8907
```

Depois, abra `http://localhost:8907/`. Para obter arquivos ausentes durante a primeira execução, o servidor precisa de conexão.

## Estrutura

| Caminho | Função |
| --- | --- |
| `telechargement/index.html` | Launcher e seleção de mapas |
| `telechargement/kzzkaiii.css` | Interface dark glass responsiva |
| `telechargement/tro-timer.js` | Cronômetro integrado |
| `telechargement/manifest.webmanifest` | Metadados de instalação web |
| `android-app/` | Projeto Android e integração Python |
| `serve_localhost.py` | Servidor local e cache |
| `pogo_outils.py` | Ferramentas existentes para o modo Pogo |
| `training_outils.py` | Ferramentas existentes para Training |

## Licenças e uso

- Consulte a licença e os avisos do Volt Maps no repositório de origem.
- Subway Surfers, seus mapas, builds, personagens, sons e demais recursos pertencem aos respectivos titulares, incluindo a SYBO Games.
- Não publique nem redistribua builds ou recursos de terceiros sem autorização.
- O nome KzzKaiii identifica esta adaptação pessoal; não indica afiliação oficial com SYBO Games.

---

**Status:** interface web adaptada; APK Android de depuração compilado com sucesso em 09/11/2026. Teste em aparelho físico pendente.

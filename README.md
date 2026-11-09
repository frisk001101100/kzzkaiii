# KzzKaiii — Volt Maps

Fork pessoal do **Volt Maps**, organizado para evoluir em uma experiência própria para Android.

O projeto mantém como base o espelho local do player Subway Surfers WebGL Reborn. Esta cópia é um trabalho em andamento: a prioridade é preservar o funcionamento do projeto original antes de adicionar mudanças na interface e no empacotamento.

> **Créditos:** projeto-base criado por Volt. Este repositório é uma cópia modificada para uso e desenvolvimento pessoal. Os créditos e avisos dos autores originais devem ser preservados.

## Estado atual

Recursos já presentes na base recebida:

- Launcher web com galeria e seleção de mapas.
- Modo Enhanced e rotas de jogo disponíveis na base.
- Modo Pogo em mapas compatíveis.
- Modo Training — atualmente documentado para Beijing.
- Timer integrado ao jogo, com eventos de início de partida, coleta de moeda, morte e pausa.
- Cache local para reutilizar arquivos já baixados.

**Cobertura documentada pela versão-base:** 81 mapas jogáveis de 96 listados. A disponibilidade varia por mapa e por conteúdo existente no projeto.

## Próximos objetivos

Estes itens são metas de desenvolvimento, não funcionalidades já entregues:

- [ ] Criar uma interface própria para o launcher.
- [ ] Preparar uma versão Android instalável (APK).
- [ ] Organizar os modos Original e Training na experiência mobile.
- [ ] Adaptar o timer para telas sensíveis ao toque e controles mobile.
- [ ] Testar inicialização, cache e funcionamento offline no Android.

## Execução local

A base atual usa Python para iniciar o servidor local:

```bash
python3 installe.py
python3 serve_localhost.py 8907
```

Depois, abra:

```
http://localhost:8907/
```

O hostname `localhost` é importante para o funcionamento esperado do player. A instalação e o pré-carregamento de mapas podem exigir conexão com a internet.

## Timer

O projeto-base inclui `telechargement/tro-timer.js`. O timer recebe eventos do jogo para acompanhar o estado da partida, incluindo início, pausa, retomada, coleta de moeda e fim de run. A interface e os controles ainda precisam ser validados em dispositivos Android.

## Estrutura importante

| Caminho | Função |
| --- | --- |
| `telechargement/` | Launcher, páginas e arquivos do player |
| `telechargement/tro-timer.js` | Timer integrado |
| `serve_localhost.py` | Servidor local |
| `installe.py` | Instalação e verificação |
| `pogo_outils.py` | Ferramentas do modo Pogo |
| `training_outils.py` | Ferramentas do modo Training |

## Avisos e licenças

- Este repositório deriva do **Volt Maps**; consulte o projeto de origem para histórico, autoria e condições de uso.
- Subway Surfers e seus elementos de jogo pertencem aos respectivos titulares, incluindo a SYBO Games.
- Não presuma que os arquivos de jogo, mapas, builds ou recursos de terceiros possam ser redistribuídos ou hospedados publicamente. Respeite as permissões e licenças aplicáveis.
- A criação do APK e as alterações listadas em “Próximos objetivos” ainda não estão concluídas.

---

**Projeto:** KzzKaiii  
**Base:** Volt Maps  
**Status:** em desenvolvimento

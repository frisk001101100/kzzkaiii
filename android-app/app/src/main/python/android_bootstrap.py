"""Inicializa o servidor local do KzzKaiii dentro do processo Android."""
import os
import sys
from http.server import ThreadingHTTPServer


def start_server(app_root, port=8907):
    root = os.path.abspath(app_root)
    site = os.path.join(root, "telechargement")
    cache = os.path.join(root, "cache")
    os.makedirs(cache, exist_ok=True)

    # O servidor existente faz cache local dos arquivos que o player solicita.
    # A primeira execução precisa de rede; os arquivos baixados ficam no storage
    # privado do app e podem ser reutilizados nas próximas partidas.
    os.environ["ONLINE"] = "1"
    os.environ["ACTIV_RESEAU"] = "1"
    os.environ["HOST"] = "127.0.0.1"
    sys.argv = ["serve_localhost.py", str(int(port))]

    import serve_localhost

    serve_localhost.RACINE = site
    serve_localhost.CACHE = cache
    serve_localhost.HOST = "127.0.0.1"
    serve_localhost.PORT = int(port)
    serve_localhost.OFFLINE = False

    server = ThreadingHTTPServer(("127.0.0.1", int(port)), serve_localhost.Gestionnaire)
    server.serve_forever()

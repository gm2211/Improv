#!/usr/bin/env python
import nrepl

def get_client(ip, port):
    ip = "127.0.0.1" if ip == "localhost" else ip
    try:
        port = int(port)
    except ValueError:
        print("Port should be an integer")
        import sys
        sys.exit(-1)
    return nrepl.WatchableConnection(nrepl.connect("nrepl://{}:{}".format(ip, port)))

def add_resp (session, msg, outs={}):
    out = msg.get("out", None)
    if out: 
        outs[session].append(out)

def watch_new_sessions (msg, wc, key, outs={}):
    session = msg.get("new-session")
    outs[session] = []
    wc.watch("session" + session, {"session": session},
            lambda msg, wc, key: add_resp(session, msg, outs))

def create_message(command):
    return {"op": "eval", "code": "{}".format(command)}

def send_message(content, ip="", port=0, client=None):
    client = client or get_client(ip, port)
    msg = create_message(content)
    client.send(msg)


if __name__ == "__main__":
    import functools as ft
    outs = {}
    ip = raw_input("Server IP: ")
    port = raw_input("Server port: ")
    client = get_client(ip, port)
    client.watch("sessions", {"new-session": None}, ft.partial(watch_new_sessions, outs=outs))

    while True:
        content = raw_input("Message content: ")
        send_message(client=client, content=content)
        print outs

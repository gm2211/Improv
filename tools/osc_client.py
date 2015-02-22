#!/usr/bin/env python3
from pythonosc import osc_message_builder
from pythonosc import udp_client

def get_client(ip, port):
    try:
        port = int(port)
    except ValueError:
        print("Port should be an integer")
        import sys
        sys.exit(-1)
    return udp_client.UDPClient(ip, port)

def create_message(address, arg):
    msg = osc_message_builder.OscMessageBuilder(address = address)
    msg.add_arg(arg)
    return msg.build()

def send_message(address, content, ip="", port=0, client=None):
    client = client or get_client(ip, port)
    msg = create_message(address, content)
    client.send(msg)


if __name__ == "__main__":
    ip = input("Server IP: ")
    port = input("Server port: ")
    message_address = input("Message address: ")
    client = get_client(ip, port)

    while True:
        content = input("Message content: ")
        send_message(client=client, address=message_address, content=content)

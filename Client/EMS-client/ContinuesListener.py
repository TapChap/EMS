import threading, clientUtil

class ContinuesListener(threading.Thread):
    def __init__(self, targetName):
        self.name = targetName

    def run(self):
        incoming = clientUtil.receive()

        if incoming["status"] == "end":
            return

        print(f"{self.name}: {incoming["message"]}")

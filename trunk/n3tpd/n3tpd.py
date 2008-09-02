from twisted.internet import reactor
from news import *
from database import *

    

myServer = UsenetServerFactory(PickleStorage("nntp.pickle", groups = (Group("test.misc"), Group("test.misc.bla"))))


reactor.listenTCP(9119, myServer)
reactor.run()

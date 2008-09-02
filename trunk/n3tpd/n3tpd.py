from twisted.internet import reactor
from news import *
from database import *

backend_cred = {}
backend_cred['dbapiName'] = "MySQLdb"
backend_cred['user'] = "root"
backend_cred['db'] = "n3tpd_data"

#backend = PickleStorage("nntp.pickle", groups = (Group("test.misc"), Group("test.misc.bla")))
#backend = NewsShelf("testhost", "data")
backend = NewsStorageAugmentation(backend_cred)

#backend.addGroup("test.misc", "n")
#backend.addGroup("test.misc.bla", "n")
myServer = UsenetServerFactory(backend)


reactor.listenTCP(9119, myServer)
reactor.run()

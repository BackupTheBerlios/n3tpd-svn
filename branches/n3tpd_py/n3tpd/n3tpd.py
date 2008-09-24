from twisted.internet import reactor
from news import *
from database import *

backend_cred = {}
backend_cred['dbapiName'] = "MySQLdb"
backend_cred['user'] = "root"
backend_cred['db'] = "n3tpd_data"

backend = NewsStorageAugmentation(backend_cred)

myServer = UsenetServerFactory(backend)

reactor.listenTCP(9119, myServer)
reactor.run()

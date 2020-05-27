**Proves nivell 3**
Si el nivell 3 funciona correctament podem estar segurs que amb nivells inferiors també ha de funcionar correctament.
Analitzarem multiples vegades els moviments escollits per la cpu amb nivell i mirarem si escull correctament. El fitxer carregat és [saved_games/comprovacionsEleccionsMinMax2.json](https://github.com/udg-propro/projecte-2020-a3/blob/master/src/saved_games/comprovacionsEleccionsMinMax2.json).
![](https://i.imgur.com/rACf2gf.png)
(nivell 1 -> color blau)
(nivell 2 -> color vermell)
(nivell 3 -> color negre)
Elecció 1:
![enter image description here](https://i.imgur.com/uBa3pYy.png)
El cavall no el pot moure sino provocaria escac al rei i per tant les negres podrien fer cualsevol cosa menys moure el cavall i llavores al següent torn la fitxa blanca podria matar al cavall. El que no detecta es que el rei se'l menjaria tot seguit i la puntuacio seria nul·la, pero com que el nivell és 3, fins aqui arriba. 
Altres eleccions:
![enter image description here](https://i.imgur.com/FXMdfPc.png)![enter image description here](https://i.imgur.com/8MfIXEc.png)
![enter image description here](https://i.imgur.com/cFcwXLh.png)
Si ens hi fixem son moviments que al següent torn mataran una peça si o si, algunes de més punts, pero al següent torn poden perdre una altre i la puntuació resultant seria 1.


Un altre prova amb la partida guardada [saved_games/comprovacionsEleccionsMinMax2.json](https://github.com/udg-propro/projecte-2020-a3/blob/master/src/saved_games/comprovacionsEleccionsMinMax3.json). Aquí tenim la següent situació interessant en que s'ha vist envolicat el jugador blanc:
![enter image description here](https://i.imgur.com/GCGAcAy.png)
Les blanques detectan que tenen l'alfil amenaçat per tots costats i l'acabrien matant a no ser que fagi el moviment que ha escollit i fent-lo detecta inclús que a nivell 3 podria arribar a matar alguna peça depenent de la situació. 
Tot seguit tiren les negres i escullen el següent moviment:
![enter image description here](https://i.imgur.com/5xcGAYp.png)
Aixó fa que cualsevol moviment possible de l'alfil acabi amb ell morint al final. Obviament algun moviment portaria la mort de les peçes blanques pero aixó a nivell 4 i no hi arribem. És a dir les negres han fet un bon moviment i ja després al següent torn analitzaran la situacio a escollir.

No faig més proves ja que és molt dificil arribar a provar-ho més extens pero com a minim puc estar segur de que l'algorisme ha funcionat bé en totes aquestes ocasions.

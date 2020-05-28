<!DOCTYPE html>
<html>

<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>DocumentacioProvesCpu (1).md.md</title>
  <link rel="stylesheet" href="https://stackedit.io/style.css" />
</head>

<body class="stackedit">
  <div class="stackedit__html"><h2 id="cpu">Cpu:</h2>
<p><strong>Descripció:</strong><br>
La CPU és l’encarraga de simular un jugador amb o sense un coneixament de partides guanyadores, les classes utilitzades són:</p>
<ul>
<li><a href="https://github.com/udg-propro/projecte-2020-a3/blob/master/src/Cpu.java">Cpu.java</a>: Classe que decideix com actuar depenent de la situació del torn. La seva funcionalitat és mirar si el seu coneixament té guardada la situació actual del tauler i, si la té, escolleix el moviment associat a aquella situació. Altrament aplica un algorisme (MinMax) encarragat de trobar el moviment més óptim fins a un cert nivell de profunditat.</li>
<li><a href="https://github.com/udg-propro/projecte-2020-a3/blob/master/src/Knowledge.java">Knowledge.java</a>: Classe per emmagatzemar seqüencies de tirades guanyadores.</li>
</ul>
<p><strong>Demostració Coneixament (Knowledge):</strong><br>
Per validar el funcionament del coneixament he provat cada un dels fitxers de coneixament que hi ha predefinits i manaulment he seguit les seqüencies per validar qué es seguien correctament. Per fer-ho he preparat el següent script powershell  <a href="https://github.com/udg-propro/projecte-2020-a3/blob/master/tests/testKnowledge.PS1">tests/testKnowledge.PS1</a>, qué realitza la tasca automàticament per aixi no haver d’entrar-ho cada cop. En l’script també es valida la possible complementació de fitxers carregats.<br>
Per poder executar s’han de comentar unes línies de codi (explicat tot seguit). De totes formes els outputs de l’execució del scrpit són a  <a href="https://github.com/udg-propro/projecte-2020-a3/tree/master/tests/knowledgeOutputsResults">tests/knowledgeOutputsResults</a>.<br>
Per poder executar s’han de comentar de la línia 549 a la línia 560 (System.out + readLine + switch) del fitxer <a href="https://github.com/udg-propro/projecte-2020-a3/blob/master/src/ConsoleGame.java">src/ConsoleGame.java</a> i també la línia 1086 (String  s = readInputLine(false);) i subsituir-ho per String s=“N”; del mateix fitxer anteiror per evitar interrupcions durant l’execució.</p>
<blockquote>
<p>Amb complementació em refereixo qué per exemple una seqüencia de tirades al començament pot esser igual en dos fitxers de coneixament diferents peró que a partir d’un punt la seqüencia varï depenent del moviment del contrincant. Exemple: <a href="https://github.com/udg-propro/projecte-2020-a3/blob/master/src/data/knowledge/pastorBlanques.json">pastorBlanques.json</a><br>
<a href="https://github.com/udg-propro/projecte-2020-a3/blob/master/src/data/knowledge/pastorComplementariBlanques.json">pastorComplementariBlanques.json</a></p>
</blockquote>
<p><strong>Demostració MinMax:</strong><br>
<em>(Per totes les proves en aquest punt es modificaran els fitxers de configuració amb un limitEscacsSeguits:15 per ser bastant permisius i també un limitTornsInacció:35 (si s’introdueix un número &gt;= 40 no hi ha limit) per evitar bucles de partides amb només 2 reis, etc.</em></p>
<p><em><strong>1. Comprovacions d’eleccions de moviments:</strong></em></p>
<p>Per demostrar el correcte funcionament del MinMax carragarem la partida guardada <a href="https://github.com/udg-propro/projecte-2020-a3/blob/master/src/saved_games/comprovacionsEleccionesMinMax1.json">src/saved_games/comprovacionsEleccionesMinMax1.json</a> per analitzar la situació i comprovar que el minMax escolleix correctament amb diferents nivells.</p>
<p><em>Nivell 1:</em><br>
Només mira puntuacions possibles que pot obtenir en el seu torn i te en compte el torn del contrincant.<br>
<img src="https://i.imgur.com/1kokOFB.png" alt="enter image description here"><br>
Observem qué efectivament escolleix el moviment que te puntuació més alta i l’únic que pot matar una peça, pero al escollir només 1 nivell no preveu que ha sacrificat un alfil només per matar un peo.</p>
<p><em>Nivell 2:</em><br>
En aquest nivell acumula una puntuació positiva per els seus moviments i simula el següent torn per veure si aquell moviment resta puntuació.<br>
<img src="https://i.imgur.com/Vspqh6C.png" alt="enter image description here"><br>
Observem que realitza el moviment més segur. És a dir tots els moviments possibles al primer nivell donavem 0 de puntuació, pero ara oidràn donaran negatiu ja que al següent moviment al 0 li hauriem de restar la puntuació d’haver perdut un alfil. El mateix passa amb el moviment de matar el peo amb l’afil, guanyariem 1 peró perdriem la puntuació de l’alfil i aixo donaria lloc a una puntuació negativa. En canvi si fem el moviment que ha escollit l’algorisme tenim una puntuació de 0.</p>
<p><em>Nivell 3:</em><br>
Per última nem a fer una prova amb el nivell 3. És el mateix que en els dos anteriors peró a més es torna a mirar un altre cop tots els moviments possibles després d’aplicar els dos moviments anteriors.<br>
<img src="https://i.imgur.com/IMEAVXg.png" alt="enter image description here"><br>
Aquest és una mica més complex de veure. Per seguir-lo hem de tenir clar els dos passos anteriors. Observem que escull el moviment fins a d3(linea blava) en comptes de c4(linea blava en la foto anterior nivell 2) aixó és perqué un cop analitzat el punt anterior també analitza que passaria al següent nivell i veu que si va a d3 pot matar un peó segur (linea verda).<br>
Ens podriem preguntar perqué no ha escollit anar a c4(linea groga) i aixi poder matar el cavall. La explicació no és molt complexa, hem de tenir en compte que al nivell 2 el contrincant també escolliria optimitzant i no es dexiaria matar el cavall. Ara bé, si escollim el recorregut del peó, si el contrincant al nivell 2 mou el peó a derrera en té un altre per matar.</p>
<p>He fet més proves d’aquest estil al document per estar segur que l’algorisme funciona correctament.</p>
<p><em><strong>2. Factor d’aleatoritat:</strong></em><br>
Si ens imaginem una partida de 2 cpus, les dos amb nivell 1  en la següent situació:<br>
<img src="https://i.imgur.com/eZMiMsT.png" alt="enter image description here"><br>
Com hem explicat abans la Cpu amb nivell 1 analitza tots els moviments possibles que pot fer i quin puntuatge en pot extreure. En la situació anterior no hi ha cap que dongui un puntuatge superior a 0 ni per blanques ni per negres. Doncs bé, si ens imaginem que la cpu comença sempre a mirar els moviments a partir de la torra analitzant els seus moviments desde els de més a prop fins a més llunyans i escolleix de forma que es queda amb el més gran que trobi es podria donar el cas següent:<br>
<img src="https://i.imgur.com/rcT1CRh.png" alt="enter image description here"><br>
Observem que com que primer mira la torra sempre farà el moviment de la linia negra i en el següent torn el moviment de la linea vermella ens el cas dels dos jugadors ja que tots els altres moviments(lineas blaves) també tenen una puntuació de 0 pero cap te una més gran. Per evitar aixó el que es fa és en el primer nivell guardar-se en una taula tots els moviments amb la última puntuació més alta i al final s’escolleix un aleatori. Així és menys probable arribar a situacions similars.</p>
<blockquote>
<p>En l’algorisme intern aquesta elecció només es fa al nivell 1 ja que en nivells més profunds només es fa la simulació per extreure la puntuació, no s’escull el moviment.</p>
</blockquote>
<p>Per demostrar que aixo és realment aleatori és tan simple com executar varis cops una partida on la cpu sigui la primera en tirar i veure que el primer moviment sempre és diferent que en l’execució anterior.<br>
<img src="https://i.imgur.com/dlPeV6h.png" alt="Execució 1"><br>
<img src="https://i.imgur.com/qXiXzSX.png" alt="Execució 2"></p>
<p>He fet un parell més d’analizis documentats al següent html<a href="https://github.com/udg-propro/projecte-2020-a3/blob/master/doc/mes-ProvesMinMax.md">doc/mes-ProvesMinMax.md</a></p>
<p><strong>3. Cpu+Coneixament vs Cpu normal:</strong>*<br>
Havia creat el següent powershell script <a href="https://github.com/udg-propro/projecte-2020-a3/blob/master/tests/testMinMaxWithKnowledge.PS1">tests/testMinMaxWithKnowledge.PS1</a> per provar CPU amb coneixement vs CPU sense o les dos amb coneixament. Llavores observar els cops que escolleixen moviments desde el coneixament, peró és dificil que ho fagin (menys al inici del joc) ja que hi ha moltes situacions possibles i tenir-les totes guardades és complicat. Havia guardat 10 partides guanyades per blanques i per negres executant partides cpu vs cpu peró es necesiten molts més fitxers de coneixament per poder observar eleccions del coneixament.</p>
<p><em><strong>4. Més proves:</strong></em><br>
També he fet el següent powershell script <a href="https://github.com/udg-propro/projecte-2020-a3/blob/master/tests/testMinMax.PS1">tests/testMinMax.PS1</a> per tal de fer més proves amb diferents combinacions de nivells i colors alternats de forma més autómatica.<br>
Per poder executar s’han de comentar unes línias de codi (explicat tot seguit). De totes formes els outputs de varies execucions del scrpit són a <a href="https://github.com/udg-propro/projecte-2020-a3/tree/master/tests/minMaxOutputsResults">tests/minMaxOutputsResults</a>.<br>
Per poder executar s’han de comentar de la linia 549 a la linia 560(System.out + readLine + switch) del fitxer <a href="https://github.com/udg-propro/projecte-2020-a3/blob/master/src/ConsoleGame.java">src/ConsoleGame.java</a> i també la linia 1086(String  s = readInputLine(false);) i subsituir-ho per String s=“N”; del mateix fitxer anteiror, per evitar interrupcions durant l’execució.</p>
<blockquote>
<p>L’execució d’aquest script crea processos que s’executen asíncronament, consumiex bastanta cpu i els resultats s’emmagtzeman en fitxers de text. Pot tardar alguns minuts.</p>
</blockquote>
<p><em><strong>5. Temps d’execució:</strong></em><br>
Petit estudi sobre els temps d’execució. Obviament les mostres són molt petites i les dades no reflectiran la realitat del tot peró ens podran donar una idea aproximada. L’objectiu és observar com al augmentar el nivell augmenta exponencialment el temps.</p>
<blockquote>
<p>S’ha de tenir en compte que hi han moments de la partida més crítics que altres. És a dir, hi ha moments de la partida que tens més peces o menys i en pots moure més o menys. Aixó afecta directament al temps d’execució.<br>
Exemple: Al inici de la partida podem moure només els peons i els cavalls, pero al cap d’haver fet més moviments es podrán moure més fitxes i aixo farà incrementar el temps.</p>
</blockquote>
<p>Nivell 1:</p>

<table>
<thead>
<tr>
<th>Execució</th>
<th>Temps màxim</th>
<th>Temps mínim</th>
<th>Mitjana de temps</th>
<th>Total moviments</th>
<th>Resultat</th>
<th>Fitxer</th>
</tr>
</thead>
<tbody>
<tr>
<td>1</td>
<td>0.034</td>
<td>0.0012</td>
<td>0.0051</td>
<td>117</td>
<td>Empat</td>
<td><a href="https://github.com/udg-propro/projecte-2020-a3/blob/master/tests/minMaxOutputsResults/testMinMax_niv1_vs_niv1_1.txt">tests/minMaxOutputsResults/testMinMax_niv1_vs_niv1_1.txt</a></td>
</tr>
<tr>
<td>2</td>
<td>0.019</td>
<td>0.001</td>
<td>0.0048</td>
<td>117</td>
<td>Empat</td>
<td><a href="https://github.com/udg-propro/projecte-2020-a3/blob/master/tests/minMaxOutputsResults/testMinMax_niv1_vs_niv1_1.txt">tests/minMaxOutputsResults/testMinMax_niv1_vs_niv1_1.txt</a></td>
</tr>
<tr>
<td>3</td>
<td>0.017</td>
<td>0.001</td>
<td>0.0036</td>
<td>24</td>
<td>Perdedor</td>
<td><a href="https://github.com/udg-propro/projecte-2020-a3/blob/master/tests/minMaxOutputsResults/testMinMax_niv1_vs_niv2_1.txt">tests/minMaxOutputsResults/testMinMax_niv1_vs_niv2_1.txt</a></td>
</tr>
<tr>
<td>4</td>
<td>0.018</td>
<td>0.001</td>
<td>0.0039</td>
<td>20</td>
<td>Perdedor</td>
<td><a href="https://github.com/udg-propro/projecte-2020-a3/blob/master/tests/minMaxOutputsResults/testMinMax_niv1_vs_niv3_1.txt">tests/minMaxOutputsResults/testMinMax_niv1_vs_niv3_1.txt</a></td>
</tr>
<tr>
<td>5</td>
<td>0.0076</td>
<td>0.0011</td>
<td>0.0035</td>
<td>35</td>
<td>Perdedor(Rei Ofegat)</td>
<td><a href="https://github.com/udg-propro/projecte-2020-a3/blob/master/tests/minMaxOutputsResults/testMinMax_niv2_vs_niv1_1.txt">tests/minMaxOutputsResults/testMinMax_niv2_vs_niv1_1.txt</a></td>
</tr>
<tr>
<td>6</td>
<td>0.017</td>
<td>0.0013</td>
<td>0.008</td>
<td>49</td>
<td>Perdedor(Rei ofegat)</td>
<td><a href="https://github.com/udg-propro/projecte-2020-a3/blob/master/tests/minMaxOutputsResults/testMinMax_niv3_vs_niv1_1.txt">tests/minMaxOutputsResults/testMinMax_niv3_vs_niv1_1.txt</a></td>
</tr>
</tbody>
</table><blockquote>
<p>Observem que l’elecció més ràpida ha necesitat 0.001s segons, la més lenta 0.034s i si calculem la mitajana de temps de totes les mitjanes ens surt una mitjana de temps d’elecció de 0.0048s.</p>
</blockquote>
<p>Nivell 2:</p>

<table>
<thead>
<tr>
<th>Execució</th>
<th>Temps màxim</th>
<th>Temps mínim</th>
<th>Mitjana de temps</th>
<th>Total moviments</th>
<th>Resultat</th>
<th>Fitxer</th>
</tr>
</thead>
<tbody>
<tr>
<td>1</td>
<td>0.15</td>
<td>0.025</td>
<td>0.067</td>
<td>24</td>
<td>Guanyador</td>
<td><a href="https://github.com/udg-propro/projecte-2020-a3/blob/master/tests/minMaxOutputsResults/testMinMax_niv1_vs_niv2_1.txt">tests/minMaxOutputsResults/testMinMax_niv1_vs_niv2_1.txt</a></td>
</tr>
<tr>
<td>2</td>
<td>0.23</td>
<td>0.016</td>
<td>0.11</td>
<td>36</td>
<td>Guanyador(Rei ofegat)</td>
<td><a href="https://github.com/udg-propro/projecte-2020-a3/blob/master/tests/minMaxOutputsResults/testMinMax_niv2_vs_niv1_1.txt">tests/minMaxOutputsResults/testMinMax_niv2_vs_niv1_1.txt</a></td>
</tr>
<tr>
<td>3</td>
<td>1.64</td>
<td>0.053</td>
<td>0.58</td>
<td>60</td>
<td>Guanyador</td>
<td><a href="https://github.com/udg-propro/projecte-2020-a3/blob/master/tests/minMaxOutputsResults/testMinMax_niv2_vs_niv2_1.txt">tests/minMaxOutputsResults/testMinMax_niv2_vs_niv2_1.txt</a></td>
</tr>
<tr>
<td>4</td>
<td>1.86</td>
<td>0.037</td>
<td>0.45</td>
<td>60</td>
<td>Perdedor</td>
<td><a href="https://github.com/udg-propro/projecte-2020-a3/blob/master/tests/minMaxOutputsResults/testMinMax_niv2_vs_niv2_1.txt">tests/minMaxOutputsResults/testMinMax_niv2_vs_niv2_1.txt</a></td>
</tr>
<tr>
<td>5</td>
<td>0.72</td>
<td>0.038</td>
<td>0.32</td>
<td>61</td>
<td>Perdedor</td>
<td><a href="https://github.com/udg-propro/projecte-2020-a3/blob/master/tests/minMaxOutputsResults/testMinMax_niv2_vs_niv3_1.txt">tests/minMaxOutputsResults/testMinMax_niv2_vs_niv3_1.txt</a></td>
</tr>
<tr>
<td>6</td>
<td>0.50</td>
<td>0.021</td>
<td>0.17</td>
<td>77</td>
<td>Perdedor(Rei ofegat)</td>
<td><a href="https://github.com/udg-propro/projecte-2020-a3/blob/master/tests/minMaxOutputsResults/testMinMax_niv3_vs_niv2_1.txt">tests/minMaxOutputsResults/testMinMax_niv3_vs_niv2_1.txt</a></td>
</tr>
</tbody>
</table><blockquote>
<p>Observem que l’elecció més ràpida ha necesitat 0.016s segons, la més lenta 1.86s i  si calculem la mitajana de temps de totes les mitjanes ens surt una mitjana de temps d’elecció de 0.28s.</p>
</blockquote>
<p>Nivell 3:</p>

<table>
<thead>
<tr>
<th>Execució</th>
<th>Temps màxim</th>
<th>Temps mínim</th>
<th>Mitjana de temps</th>
<th>Total moviments</th>
<th>Resultat</th>
<th>Fitxer</th>
</tr>
</thead>
<tbody>
<tr>
<td>1</td>
<td>11.90</td>
<td>0.24</td>
<td>2.2</td>
<td>20</td>
<td>Guanyador</td>
<td><a href="https://github.com/udg-propro/projecte-2020-a3/blob/master/tests/minMaxOutputsResults/testMinMax_niv1_vs_niv3_1.txt">tests/minMaxOutputsResults/testMinMax_niv1_vs_niv3_1.txt</a></td>
</tr>
<tr>
<td>2</td>
<td>20.85</td>
<td>0.72</td>
<td>7.48</td>
<td>61</td>
<td>Guanyador</td>
<td><a href="https://github.com/udg-propro/projecte-2020-a3/blob/master/tests/minMaxOutputsResults/testMinMax_niv2_vs_niv3_1.txt">tests/minMaxOutputsResults/testMinMax_niv2_vs_niv3_1.txt</a></td>
</tr>
<tr>
<td>3</td>
<td>14.23</td>
<td>0.31</td>
<td>5.85</td>
<td>50</td>
<td>Guanyador(Rei Ofegat)</td>
<td><a href="https://github.com/udg-propro/projecte-2020-a3/blob/master/tests/minMaxOutputsResults/testMinMax_niv3_vs_niv1_1.txt">tests/minMaxOutputsResults/testMinMax_niv3_vs_niv1_1.txt</a></td>
</tr>
<tr>
<td>4</td>
<td>11.03</td>
<td>0.57</td>
<td>4.10</td>
<td>78</td>
<td>Guanyador(Rei Ofegat)</td>
<td><a href="https://github.com/udg-propro/projecte-2020-a3/blob/master/tests/minMaxOutputsResults/testMinMax_niv3_vs_niv2_1.txt">tests/minMaxOutputsResults/testMinMax_niv3_vs_niv2_1.txt</a></td>
</tr>
<tr>
<td>5</td>
<td>10.69</td>
<td>0.47</td>
<td>4.22</td>
<td>95</td>
<td>Guanyador(Rei ofegat)</td>
<td><a href="https://github.com/udg-propro/projecte-2020-a3/blob/master/tests/minMaxOutputsResults/testMinMax_niv3_vs_niv3_1.txt">tests/minMaxOutputsResults/testMinMax_niv3_vs_niv3_1.txt</a></td>
</tr>
<tr>
<td>6</td>
<td>17.60</td>
<td>0.09</td>
<td>5.00</td>
<td>95</td>
<td>Perdedor(Rei Ofegat)</td>
<td><a href="https://github.com/udg-propro/projecte-2020-a3/blob/master/tests/minMaxOutputsResults/testMinMax_niv3_vs_niv3_1.txt">tests/minMaxOutputsResults/testMinMax_niv3_vs_niv3_1.txt</a></td>
</tr>
</tbody>
</table><blockquote>
<p>Observem que l’elecció més ràpida ha necesitat 0.09s segons, la més lenta 20.85s i  si calculem la mitajana de temps de totes les mitjanes ens surt una mitjana de temps d’elecció de 4.80s.</p>
</blockquote>
<p>Hem limitat el joc a 3 nivells ja que si fiquem 4 el temps màxim i la mitjana pot arribar ser excessiu.<br>
La prova que he fet és aquesta <a href="https://github.com/udg-propro/projecte-2020-a3/blob/master/tests/minMaxOutputsResults/testMinMax_niv4_vs_niv4.txt">tests/minMaxOutputsResults/estMinMax_niv4_vs_niv4.txt</a>, per fer-ho he modificat el codi per qué en comptes de nivell 3 sigui 4.</p>
<blockquote>
<p>Observem que l’elecció de temps més lenta en una cpu és 204s i 216s i la mitajana de 50s i 72s.</p>
</blockquote>
<p><em><strong>6. Coherencia dels resultats CPU vs CPU:</strong></em><br>
Si observem els resultats anteriors podrem veure que els nivells més grans han guanyat sempre. Tot i aixi podria ser que no guanyessin o que arribessin a un empat. Tot i que quan més gran sigui la diferencia entre nivells més possibiltiat hi haurà de que aquest guanyi.</p>
</div>
</body>

</html>

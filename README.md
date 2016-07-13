Programski zadatak: TCPPing

Zadatak: Izrada funkcionalnosti „ping“-a sa unaprijeđenim funkcionalnostima, program je namijenjen za testiranje rada IP mreže i određivanje Round Trip Time.
Programski jezik: Java ili .NET

Opis zadatka:

 program će biti pokrenut na dva neovisna računala međusobno povezana u mreži

 program mora imati dva osnovna načina rada:

1. Pitcher (bacač poruka) biti će pokrenut na „prvom - A“ računalu i ima funkcionalnost generiranja i slanja poruka prema drugom računalu

2. Catcher (lovac poruka) biti će pokrenut na „drugom - B“ računalu i ima funkcionalnost primanja i slanja odgovora na pojedinu primljenu poruku

 Program mora imati mogućnost definiranja dužine pojedine poruke te brzine slanja izražene u „msgs/s“

 Programa u načinu rada Pitcher mora izračunavati i prikazivati svake sekunde statističke informacije: (formatirani ispis u max 1-2 retka u konzolni prozor)
1. Vrijeme - HH:MM:SS

2. Ukupan broj poruka poslanih

3. Broj poruka u prethodnoj sekundi (brzina)

4. Prosječno vrijeme u prethodnoj sekundi potrebno da poruka dođe od Pitcher-a do Catcher-a (smjer A->B)

5. Prosječno vrijeme u prethodnoj sekundi potrebno da odgovor dođe od Catcher-a do Pitcher-a (smjer B->A)

6. Prosječno vrijeme u prethodnoj sekundi potrebno da poruka napravi krug (A->B->A)

7. Ukupno maksimalno vrijeme potrebno da poruka dođe od Pitcher-a do Catcher-a (A->B)

8. Ukupno maksimalno vrijeme potrebno da odgovor dođe od Catcher-a do Pitcher-a (B->A)

9. Ukupno maksimalno vrijeme potrebno da poruka napravi krug (A->B->A)

 Napomena: Vrijeme će biti izraženo u „ms“, potrebno je pratiti zasebno vrijeme u jednom i u drugom smjeru za pojedinu poruku

 Svaka poruka poslana od Pitcher-a mora biti numerirana kako bi Pitcher moga dojaviti upozorenje o porukama koje su izgubljene odnosno nije dobio odgovor od Catcher-a na tu poruku

 Programu u načinu rada Pitcher zadajemo dužinu pojedine poruke (realno dužina poruke će biti između 50 – 3000 bytes), Pitcher mora poslati poruku točno te dužine prema Catcher-u te Catcher mora formirati odgovor ponovno te dužine prema Pitcher-u, sadržaj poruke nije važan (npr. redni broj paketa + korisni podaci za izračun statistike + slova abecede ili sl.)

 Programu će se parametri zadavati preko komandne linije i program mora ispisivati statistike u konzolni prozor, neka komandni argumenti budu sljedeći:

-p
Način rada kao Pitcher

-c
Način rada kao Catcher

-port <port>
[Pitcher] TCP socket port koji će se koristiti za connect
[Catcher] TCP socket port koji će se koristiti za listen

-bind <ip_address>
[Catcher] TCP socket bind adresa na kojoj će biti pokrenut listen

-mps <rate> [Pitcher] brzina slanja izražena u „messages per second“

    Default: 1

-size <size> [Pitcher] dužina poruke

    Minimum: 50

    Maximum: 3000

    Default: 300

<hostname>
[Pitcher] ime računala na kojemu je pokrenut Catcher

Primjer pokretanja:

na računalu „kompB“: java TCPPing –c –bind 192.168.0.1 –port 9900

na računalu „kompA“: java TCPPing –p –port 9900 –mps 30 –size 1000 kompB

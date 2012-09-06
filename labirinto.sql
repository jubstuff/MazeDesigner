-- Labirinto
CREATE TABLE Labirinto
(
	codice  	  NUMBER(8)    NOT NULL,
	nome          VARCHAR2(50) NOT NULL,
	dimX          NUMBER(8)    NOT NULL,
	dimY          NUMBER(8)    NOT NULL,
	uscita        NUMBER(8),
	entrata       NUMBER(8),
	is_bozza      CHAR(1)      DEFAULT 'T'
)
;

ALTER TABLE Labirinto ADD CONSTRAINT PK_Labirinto
	PRIMARY KEY (codice);

ALTER TABLE Labirinto ADD CONSTRAINT UQ_Labirinto_nome 
	UNIQUE (nome);

-- Casella
CREATE TABLE Casella
(
	codice   NUMBER(8) NOT NULL,
	posX         NUMBER(8) NOT NULL,
	posY         NUMBER(8) NOT NULL,
	labirinto    NUMBER(8),
	tipo         CHAR(1) DEFAULT 'M' NOT NULL,
	descrizione  VARCHAR2(100)
);

ALTER TABLE Casella ADD CONSTRAINT PK_Casella
	PRIMARY KEY (codice);

-- Collegamento
CREATE TABLE Collegamento
(
	codice       NUMBER(8) NOT NULL,
	origine       NUMBER(8) NOT NULL,
	destinazione  NUMBER(8) NOT NULL
);

--Speciale
CREATE TABLE Speciale
(
	id_speciale            NUMBER(8) NOT NULL,
	modificatorePunteggio  NUMBER(8) DEFAULT 0 NOT NULL,
	tipo 				CHAR(1)  DEFAULT 'S' NOT NULL
);

ALTER TABLE Speciale ADD CONSTRAINT PK_Speciale
	PRIMARY KEY (id_speciale);



-- Domanda
CREATE TABLE Domanda
(
	id_domanda             NUMBER(8) NOT NULL,
	testo                  VARCHAR2(100) NOT NULL,
	modificatorePunteggio  NUMBER(8) DEFAULT 0 NOT NULL,
	tipo 				CHAR(1)  DEFAULT 'D' NOT NULL
);

ALTER TABLE Domanda ADD CONSTRAINT PK_Domanda
	PRIMARY KEY (id_domanda);
	
ALTER TABLE Collegamento ADD CONSTRAINT PK_Collegamento
	PRIMARY KEY (codice);

-- Risposta
CREATE TABLE Risposta
(
	id_risposta  NUMBER(8) NOT NULL,
	risposta     VARCHAR2(50) NOT NULL,
	domanda      NUMBER(8) NOT NULL
);

ALTER TABLE Risposta ADD CONSTRAINT PK_Risposta
	PRIMARY KEY (id_risposta);
	
-- Passaggio
CREATE TABLE Passaggio
(
	id_passaggio           NUMBER(8) NOT NULL,
	modificatorePunteggio  NUMBER(8) DEFAULT 0 NOT NULL,
	tipo 				CHAR(1)  DEFAULT 'P' NOT NULL
);

ALTER TABLE Passaggio ADD CONSTRAINT PK_Passaggio
	PRIMARY KEY (id_passaggio);	

-- 
-- PARTE DINAMICA
--

-- Mossa
CREATE TABLE Mossa
(
	partita       NUMBER(8) NOT NULL,
	collegamento  NUMBER(8) NOT NULL,
	ordine        NUMBER(8) NOT NULL
);

ALTER TABLE Mossa ADD CONSTRAINT PK_Mossa
	PRIMARY KEY (partita, ordine);

ALTER TABLE Mossa ADD CONSTRAINT FK_Mossa_Collegamento
	FOREIGN KEY (collegamento) REFERENCES Collegamento (codice);


-- Partita
CREATE TABLE Partita
(
	id_partita    NUMBER(8) NOT NULL,
	giocatore     VARCHAR2(50) NOT NULL,
	punteggio     NUMBER(8) DEFAULT 0 NOT NULL,
	labirinto     NUMBER(8) NOT NULL,
	is_terminata  CHAR(1) DEFAULT 'T' NOT NULL
);

ALTER TABLE Partita ADD CONSTRAINT PK_Partita
	PRIMARY KEY (id_partita);
	
ALTER TABLE Mossa ADD CONSTRAINT FK_Mossa_Partita
	FOREIGN KEY (partita) REFERENCES Partita (id_partita);


ALTER TABLE Partita ADD CONSTRAINT FK_Partita_Labirinto
	FOREIGN KEY (labirinto) REFERENCES Labirinto (codice)
	ON DELETE CASCADE;

ALTER TABLE Partita
	ADD CONSTRAINT UQ_Partita_giocator UNIQUE(giocatore);
	
-- RispostaData
CREATE TABLE RispostaData
(
	partita   NUMBER(8) NOT NULL,
	ordine    NUMBER(8) NOT NULL,
	risposta  NUMBER(8) NOT NULL
)
;
ALTER TABLE RispostaData ADD CONSTRAINT PK_RispostaData
	PRIMARY KEY (partita, ordine)
;

ALTER TABLE RispostaData ADD CONSTRAINT FK_RispostaData_Mossa
	FOREIGN KEY (partita, ordine) REFERENCES Mossa (partita, ordine);

ALTER TABLE RispostaData ADD CONSTRAINT FK_RispostaData_Risposta
	FOREIGN KEY (risposta) REFERENCES Risposta (id_risposta);
/


Create table temp
(
 id_casella   NUMBER(8)
);	

	-- Chiavi esterne
ALTER TABLE Labirinto ADD CONSTRAINT FK_Labirinto_Casella
	FOREIGN KEY (entrata) REFERENCES Casella (codice)
	ON DELETE SET NULL;

ALTER TABLE Labirinto ADD CONSTRAINT FK_Labirinto_Casella2
	FOREIGN KEY (uscita) REFERENCES Casella (codice)
	ON DELETE SET NULL;

ALTER TABLE Casella ADD CONSTRAINT FK_Casella_Labirinto
	FOREIGN KEY (labirinto) REFERENCES Labirinto (codice)
	ON DELETE CASCADE;

ALTER TABLE Collegamento ADD CONSTRAINT FK_Collegamento_Casella
	FOREIGN KEY (origine) REFERENCES Casella (codice)
	ON DELETE CASCADE;
 
ALTER TABLE Collegamento ADD CONSTRAINT FK_Collegamento_Casella2
	FOREIGN KEY (destinazione) REFERENCES Casella (codice)
	ON DELETE CASCADE;
	
ALTER TABLE Risposta ADD CONSTRAINT FK_Risposta_Domanda
	FOREIGN KEY (domanda) REFERENCES Domanda (id_domanda);


  -- Trigger & Vincoli

ALTER TABLE Casella ADD CONSTRAINT posizione_unica_labirinto
	UNIQUE(posX, posY, labirinto);

ALTER TABLE Labirinto ADD CONSTRAINT entrata_uscita
	CHECK(entrata <> uscita);


ALTER TABLE Labirinto ADD CONSTRAINT dimensioni_positive 
	CHECK(dimX > 0 AND dimY > 0);

	
ALTER TABLE Casella ADD CONSTRAINT tipo CHECK (
	tipo IN ('V', 'D', 'S', 'P','M')
);

ALTER TABLE Casella ADD CONSTRAINT UQ_Casella 
	UNIQUE(codice, tipo);

/
CREATE OR REPLACE TRIGGER esiste_percorso
AFTER UPDATE OF is_bozza ON Labirinto
For each row
WHEN (NEW.is_bozza = 'F')
	--dichiarazioni
  Declare
	v_count NUMBER;
BEGIN
	DELETE FROM Temp;
	--inserisco nella tabella Temp il nodo iniziale
	INSERT INTO Temp VALUES(:NEW.entrata);
	LOOP
		INSERT INTO Temp (
			SELECT C.destinazione
			FROM Collegamento C JOIN Temp T
			ON C.origine = T.id_casella
			WHERE C.destinazione NOT IN (
				SELECT id_casella
				FROM Temp
			)
		);
		
		EXIT WHEN SQL%NOTFOUND; --è ok perché è una insert
	END LOOP;
	
	SELECT COUNT(*)
	INTO v_count
	FROM TEMP
	WHERE id_casella = :NEW.uscita;
	
	IF v_count < 1 THEN
		raise_application_error(-20001, 'Deve esistere un percorso tra entrata e uscita');
	END IF;
END;
/
ALTER TABLE Partita
	ADD CONSTRAINT UQ_Partita_partita_labirinto
		UNIQUE(id_partita, labirinto)
;
/
-- Trigger auto_increment
create sequence codice_labirinto_sequence
increment by 1 start with 1
nomaxvalue;  
/
create or replace trigger codice_labirinto_autoincrement 
before insert on labirinto
for each row 
begin
	Select codice_labirinto_sequence.nextval into :new.codice from dual;
end;
/
create sequence codice_casella_sequence
increment by 1 start with 1
nomaxvalue;  
/
create or replace trigger codice_casella_autoincrement 
before insert on casella
for each row 
begin
	Select codice_casella_sequence.nextval into :new.codice from dual;
end;
/
create sequence codice_collegamento_sequence
increment by 1 start with 1
nomaxvalue;  
/
create or replace trigger codice_coll_autoincrement 
before insert on collegamento
for each row 
begin
	Select codice_collegamento_sequence.nextval into :new.codice from dual;
end;
/
-- Trigger inizializzazione labirinto

Create or replace trigger inizializza_labirinto
After insert on labirinto
for each row
begin
	for i in 0 .. :new.dimx - 1
	loop
		for j in 0 .. :new.dimy - 1
    loop
      insert into Casella (labirinto,posx,posy) Values (:new.codice,i,j);
		end loop;
	end loop;
end;

/
CREATE OR REPLACE TRIGGER ai_mossa_speciale
AFTER INSERT ON Mossa
FOR EACH ROW
DECLARE
  tempTipoDest casella.tipo%TYPE;
  tempDest collegamento.destinazione%TYPE;
  collPassaggio collegamento.codice%TYPE;
  v_modificatore speciale.modificatorepunteggio%TYPE;
BEGIN
	SELECT CA.tipo, CL.destinazione
	INTO tempTipoDest, tempDest
	FROM Collegamento CL JOIN casella CA
	ON CL.destinazione = CA.codice
	WHERE CL.codice= :NEW.Collegamento;
	IF tempTipoDest = 'S' THEN
		SELECT modificatorePunteggio
		INTO v_modificatore
		FROM Speciale
		WHERE id_speciale = tempDest;

		UPDATE Partita set punteggio = punteggio + v_modificatore
			WHERE id_partita = :NEW.Partita;
	END IF;
END;
/
CREATE OR REPLACE TRIGGER bi_ordine_mosse
AFTER INSERT ON Mossa
FOR EACH ROW
DECLARE
  max_ordine mossa.ordine%TYPE;
BEGIN
	SELECT MAX(ordine)
	INTO max_ordine
	FROM Mossa
	WHERE partita = :NEW.partita;
  IF max_ordine IS NULL THEN
		INSERT INTO Mossa(ordine,partita,collegamento) VALUES
		(1, :NEW.partita, :NEW.collegamento);
	ELSE
		INSERT INTO Mossa(ordine,partita,collegamento) VALUES
		(max_ordine + 1, :NEW.partita, :NEW.collegamento);
	END IF;
END;
/
CREATE OR REPLACE TRIGGER bi_speciale
BEFORE INSERT ON Speciale
FOR EACH ROW
BEGIN
    UPDATE Casella SET tipo = 'S' WHERE codice = :NEW.id_speciale;
END;
/
CREATE OR REPLACE TRIGGER ad_speciale
AFTER DELETE ON Speciale
FOR EACH ROW
BEGIN
	UPDATE Casella SET tipo = 'V' WHERE codice = :OLD.id_speciale;
END;
/
CREATE OR REPLACE TRIGGER ai_mosse
AFTER INSERT ON Mossa
FOR EACH ROW
DECLARE
  tempTipoDest casella.tipo%TYPE;
  tempDest Collegamento.destinazione%TYPE;
  collPassaggio Collegamento.codice%TYPE;
BEGIN
	SELECT CA.tipo, CL.destinazione
	INTO tempTipoDest, tempDest
	FROM Collegamento CL JOIN Casella CA
  ON CL.codice = CA.codice
	WHERE CL.codice = :NEW.Collegamento;
  
  IF tempTipoDest = 'P' THEN
		SELECT codice
		INTO collPassaggio
		FROM Collegamento
		WHERE origine = tempDest;

		INSERT INTO Mossa VALUES (:NEW.partita, collPassaggio,:NEW.ordine + 1);
	END IF;
 
END;
/
Alter table collegamento add constraint UQ_orig_dest
unique(origine,destinazione)
/


CREATE OR REPLACE PROCEDURE init_caselle_labirinto (cod_lab in number, dimx_lab in number, dimy_lab in number)
AS
BEGIN
    FOR i IN 0 .. dimx_lab - 1
    LOOP
        FOR j IN 0 .. dimy_lab - 1 
        LOOP
        INSERT INTO casella (labirinto,posx,posy) VALUES ( cod_lab, i, j);
        END LOOP;
    END LOOP;
END;
/
CREATE OR REPLACE PROCEDURE reset_labirinto (cod_lab in number, dimx_lab in number, dimy_lab in number)
IS
BEGIN
    UPDATE labirinto SET entrata=null, uscita=null, is_bozza='T'
    WHERE codice = cod_lab;
    
    DELETE FROM casella WHERE labirinto = cod_lab;
    
    init_caselle_labirinto(cod_lab, dimx_lab, dimy_lab);
END;
/



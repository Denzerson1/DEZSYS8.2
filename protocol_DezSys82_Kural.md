

# Protokoll – Document Oriented Middleware using MongoDB

Deniz Kural

## 1. Einführung

Ziel dieses Projektes war die Entwicklung einer dokumentenorientierten Middleware zur Speicherung und Abfrage von Lagerdaten mittels MongoDB. Lagerstandorte und deren Produkte sollen über eine REST-Schnittstelle zentral in einer MongoDB-Datenbank gespeichert werden. Die Datenstruktur basiert auf der vorherigen Aufgabe „GK8.1 Spring Data and ORM“, wobei diesmal eine NoSQL-Datenbank zum Einsatz kommt.

## 2. Architektur

Das System wurde auf Basis des Spring Boot Frameworks umgesetzt. Die Daten werden über REST-Endpoints angenommen und direkt in MongoDB gespeichert. Zur Entwicklung wurden zwei Hauptmodelle erstellt: `Warehouse` und `Product`.

### Datenmodell

```java
public class Product {
    private String productId;
    private String name;
    private String category;
    private int quantity;
}
```

```java
public class Warehouse {
    @Id
    private String id;
    private String location;
    private List<Product> products;
}
```

## 3. MongoDB Konfiguration

### MongoDB in Docker starten

```bash
docker pull mongo
docker run -d -p 27017:27017 --name mongo mongo
```

### Mongo Shell starten

```bash
docker exec -it mongo bash
mongosh
```

### Daten prüfen

```js
show dbs
use test
db.warehouseData.find()
```

## 4. REST API

Die folgenden Endpoints wurden umgesetzt:

### Warehouse-Endpunkte

| Methode | Endpoint        | Beschreibung                            |
| ------- | --------------- | --------------------------------------- |
| POST    | /warehouse      | Neuen Lagerstandort hinzufügen          |
| GET     | /warehouse      | Alle Lagerstandorte mit Bestand abrufen |
| GET     | /warehouse/{id} | Konkreten Lagerstandort per ID abrufen  |
| DELETE  | /warehouse/{id} | Lagerstandort per ID löschen            |

### Product-Endpunkte

| Methode | Endpoint      | Beschreibung                                       |
| ------- | ------------- | -------------------------------------------------- |
| POST    | /product      | Neues Produkt einem Lagerstandort hinzufügen       |
| GET     | /product      | Alle Produkte samt Lagerstandort abrufen           |
| GET     | /product/{id} | Konkretes Produkt über alle Lagerstandorte abrufen |
| DELETE  | /product/{id} | Produkt per ID auf einem Lagerstandort löschen     |

## 5. Fehlerquellen und Herausforderungen

Während der Implementierung sind folgende typische Fehler aufgetreten:

- 

- **Fehlende Indexierung/ID-Fehler:** Beim Löschen von Produkten kam es zu Fehlern, da die ID im Modell nicht korrekt gesetzt war.
  
  - Lösung: Nutzung der Annotation `@Id` in beiden Modellen und korrekte Übergabe der ID im Request.

- **JSON-Formatierungsfehler:** Bei manueller Eingabe im REST-Client wurden JSONs mit falscher Struktur übergeben.
  
  - Lösung: Erstellung eines gültigen Beispiel-Bodys zur Wiederverwendung.

## 6. Mongo Shell – CRUD Operationen

### Einfügen eines Produkts (Create)

```js
db.warehouseData.insertOne({
  location: "Linz",
  products: [
    { productId: "P100", name: "Monitor", category: "Elektronik", quantity: 25 }
  ]
})
```

### Lesen aller Produkte (Read)

```js
db.warehouseData.find()
```

### Lesen eines spezifischen Produkts (Read by ID)

```js
db.warehouseData.find({ "products.productId": "P100" })
```

### Aktualisieren eines Produkts (Update)

```js
db.warehouseData.updateOne(
  { "products.productId": "P100" },
  { $set: { "products.$.quantity": 50 } }
)
```

### Löschen eines Produkts (Delete)

```js
db.warehouseData.updateOne(
  { location: "Linz" },
  { $pull: { products: { productId: "P100" } } }
)
```

## 7. CRUD-Operationen – Begriffserklärung

**CRUD** steht für **Create, Read, Update, Delete** und beschreibt die grundlegenden Operationen, die in jeder datengetriebenen Anwendung benötigt werden:

- **Create:** Neue Daten anlegen (z. B. `POST /product`)

- **Read:** Daten abfragen (z. B. `GET /warehouse`)

- **Update:** Vorhandene Daten ändern (z. B. Mongo Shell `$set`)

- **Delete:** Daten löschen (z. B. `DELETE /product/{id}`)

CRUD bildet die Grundlage für Dateninteraktionen sowohl über REST als auch direkt in MongoDB.

## 8. Antworten auf Fragestellungen

**1. Vier Vorteile eines NoSQL-Repository gegenüber relationalem DBMS:**

- Flexible Datenstruktur (kein starres Schema)

- Horizontale Skalierbarkeit

- Hohe Performance bei großen Datenmengen

- Bessere Eignung für verteilte Systeme

**2. Vier Nachteile eines NoSQL-Repository gegenüber relationalem DBMS:**

- Kein standardisiertes Abfragesystem wie SQL

- Schwächere Transaktionsunterstützung (BASE statt ACID)

- Komplexere Datenvalidierung auf Anwendungsebene

- Eingeschränkte Joins und relationale Abfragen

**3. Schwierigkeiten bei der Zusammenführung von Daten:**

- Unterschiedliche Formate und Datenmodelle zwischen Lagerstandorten

- Inkonsistente IDs oder doppelte Produktnamen

- Asynchrone Datenübertragung (zeitversetzte Synchronisierung)

- Fehlende Integritätsregeln

**4. Arten von NoSQL-Datenbanken:**

- Dokumentenorientiert (z. B. MongoDB)

- Schlüssel-Wert-Datenbanken (z. B. Redis)

- Spaltenorientiert (z. B. Apache Cassandra)

- Graphdatenbanken (z. B. Neo4j)

**5. Vertreter pro Kategorie:**

- Dokumentenorientiert: MongoDB

- Key-Value: Redis

- Spaltenorientiert: Cassandra

- Graphbasiert: Neo4j

**6. CAP-Theorem:**

- **CA (Consistency + Availability):** Verfügbarkeit und Konsistenz, aber keine Partitionstoleranz (z. B. traditionelle RDBMS)

- **CP (Consistency + Partition tolerance):** Konsistenz bleibt erhalten, aber evtl. nicht jederzeit verfügbar (z. B. MongoDB)

- **AP (Availability + Partition tolerance):** Immer verfügbar, aber evtl. inkonsistent (z. B. CouchDB)

**7. Befehl: Lagerstand eines Produktes über alle Lagerstandorte anzeigen**

```js
db.warehouseData.aggregate([
  { $unwind: "$products" },
  { $match: { "products.productId": "P100" } },
  { $group: { _id: "$products.productId", total: { $sum: "$products.quantity" } } }
])
```

**8. Befehl: Lagerstand eines Produktes eines bestimmten Standorts anzeigen**

```js
db.warehouseData.find(
  { location: "Linz", "products.productId": "P100" },
  { "products.$": 1 }
)
```

## 9. Fazit

Die Umsetzung einer dokumentenorientierten Middleware mit MongoDB bietet viele Vorteile bei der flexiblen und skalierbaren Verarbeitung von heterogenen Lagerdaten. Besonders durch den Einsatz von Spring Boot in Kombination mit Docker und MongoDB konnte ein modernes, leicht erweiterbares System realisiert werden.

---

Wenn du möchtest, kann ich daraus auch ein PDF-Dokument oder eine Markdown-Datei generieren – sag einfach Bescheid.

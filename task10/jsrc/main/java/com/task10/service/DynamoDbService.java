package com.task10.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.task10.dto.*;
import com.task10.model.Reservations;
import com.task10.model.Tables;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;


public class DynamoDbService {

    public TablesResponse getAllTables(AmazonDynamoDB amazonDynamoDB) {
        DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
        Table table = dynamoDB.getTable("Tables");

        Iterator<Item> iterator = table.scan().iterator();
        TablesResponse response = new TablesResponse();
        ArrayList<Tables> tables = new ArrayList<>();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            Tables tableItem = new Tables();
            tableItem.setId(item.getInt("id"));
            tableItem.setVip((item.getNumber("isVip").equals(BigDecimal.ONE)));
            tableItem.setNumber(item.getInt("number"));
            tableItem.setMinOrder(item.getInt("minOrder"));
            tableItem.setPlaces(item.getInt("places"));
            tables.add(tableItem);
        }
        response.setTables(tables);
        return response;
    }

    public Tables getTableById(AmazonDynamoDB amazonDynamoDB, int tableId) {
        DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
        Table table = dynamoDB.getTable("Tables");

        GetItemSpec getItemSpec = new GetItemSpec().withPrimaryKey("id", tableId);
        Item item = table.getItem(getItemSpec);

        Tables tables = new Tables();
        tables.setId(item.getInt("id"));
        tables.setVip((item.getNumber("isVip").equals(BigDecimal.ONE)));
        tables.setNumber(item.getInt("number"));
        tables.setMinOrder(item.getInt("minOrder"));
        tables.setPlaces(item.getInt("places"));
        return tables;
    }

    public TableCreationResponse createTable(AmazonDynamoDB amazonDynamoDB, TablesRequest tablesRequest) {
        Tables tables = new Tables();
        tables.setId(tablesRequest.getId());
        tables.setNumber(tablesRequest.getNumber());
        tables.setMinOrder(tablesRequest.getPlaces());
        tables.setVip(tablesRequest.isVip());
        tables.setPlaces(tablesRequest.getPlaces());

        DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDB);
        mapper.save(tables);
        TableCreationResponse tableCreationResponse = new TableCreationResponse();
        tableCreationResponse.setId(tablesRequest.getId());

        return tableCreationResponse;
    }

    public ReservationsResponse getAllReservations(AmazonDynamoDB amazonDynamoDB) {
        DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
        Table table = dynamoDB.getTable("Reservations");

        Iterator<Item> iterator = table.scan().iterator();
        ReservationsResponse response = new ReservationsResponse();
        ArrayList<Reservations> reservations = new ArrayList<>();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            Reservations reservation = new Reservations();
            reservation.setReservationId(item.getString("reservationId"));
            reservation.setDate(item.getString("date"));
            reservation.setClientName(item.getString("clientName"));
            reservation.setSlotTimeEnd(item.getString("slotTimeEnd"));
            reservation.setSlotTimeStart(item.getString("slotTimeStart"));
            reservation.setPhoneNumber(item.getString("phoneNumber"));
            reservations.add(reservation);
        }
        response.setReservations(reservations);
        return response;
    }


    public ReservationCreationResponse createReservation(AmazonDynamoDB amazonDynamoDB, ReservationsRequest reservationsRequest) {
        Reservations reservations = new Reservations();
        String reservationId = UUID.randomUUID().toString();
        reservations.setReservationId(reservationId);
        reservations.setPhoneNumber(reservationsRequest.getPhoneNumber());
        reservations.setDate(reservationsRequest.getDate());
        reservations.setSlotTimeStart(reservationsRequest.getSlotTimeStart());
        reservations.setSlotTimeEnd(reservationsRequest.getSlotTimeEnd());
        reservations.setClientName(reservationsRequest.getClientName());

        DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDB);
        mapper.save(reservations);

        ReservationCreationResponse response = new ReservationCreationResponse();
        response.setReservationId(reservationId);
        return response;
    }

}

package lab3_1;

import java.util.Date;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation.ReservationStatus;

public class ReservationBuilder {

    protected Id id = Id.generate();

    private ReservationStatus status = ReservationStatus.OPENED;

    private ClientData clientData = new ClientData();

    private Date createDate = new Date();

    public ReservationBuilder addId(Id id) {
        this.id = id;
        return this;
    }

    public ReservationBuilder addStatus(ReservationStatus status) {
        this.status = status;
        return this;
    }

    public ReservationBuilder addCreateDate(Date createDate) {
        this.createDate = createDate;
        return this;
    }

    public Reservation createReservation() {
        return new Reservation(id, status, clientData, createDate);
    }
}

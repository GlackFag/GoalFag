package com.glackfag.goalfag.models;

import com.glackfag.goalfag.models.enums.GoalState;
import com.glackfag.goalfag.models.enums.TimeframeType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Entity
@Table(name = "goal")
@SecondaryTables({@SecondaryTable(name = "goal_limitation", pkJoinColumns = @PrimaryKeyJoinColumn(name = "goal_id")),
        @SecondaryTable(name = "goal_state", pkJoinColumns = @PrimaryKeyJoinColumn(name = "goal_id"))})
public class Goal {
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Getter
    @Setter
    @Column(name = "essence")
    private String essence;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person creator;

    @Getter
    @Setter
    @Column(name = "person_id", insertable=false, updatable=false)
    private Long personId;

    @Getter
    @Setter
    @Column(table = "goal_limitation", name = "creation_date")
    private Date creationDate;

    @Getter
    @Setter
    @Column(table = "goal_limitation", name = "expired_date")
    private Date expiredDate;

    @Getter
    @Setter
    @Column(table = "goal_state", name = "state")
    @Enumerated(value = EnumType.STRING)
    private GoalState state;

    @Getter
    @Setter
    @Column(name = "timeframe")
    @Enumerated(value = EnumType.STRING)
    private TimeframeType timeframe;

    public Goal(Long id, String essence, Person creator) {
        this.id = id;
        this.essence = essence;
        this.creator = creator;
    }

    public Goal() {
    }


}

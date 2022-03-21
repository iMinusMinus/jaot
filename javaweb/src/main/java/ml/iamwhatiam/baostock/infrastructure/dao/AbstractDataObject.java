package ml.iamwhatiam.baostock.infrastructure.dao;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Refer spring-data-commons: @Id, @CreatedDate, @CreatedBy, @LastModifiedDate, @LastModifiedBy
 *
 * @author iMinusMinus
 */
@Getter
@Setter
@ToString
public abstract class AbstractDataObject implements Serializable {

    protected long id;

    protected LocalDateTime createdDate;

    protected String createdBy;

    protected LocalDateTime lastModifiedDate;

    protected String lastModifiedBy;
}

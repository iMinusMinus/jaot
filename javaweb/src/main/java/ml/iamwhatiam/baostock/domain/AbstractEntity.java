package ml.iamwhatiam.baostock.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public abstract class AbstractEntity implements Serializable {

    protected long id;

}

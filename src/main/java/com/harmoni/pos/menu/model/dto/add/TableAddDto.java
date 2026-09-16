package com.harmoni.pos.menu.model.dto.add;

import com.harmoni.pos.menu.model.dto.TableDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Data transfer object for adding a new Table.
 * Inherits properties from {@link TableDto}.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TableAddDto extends TableDto {

}
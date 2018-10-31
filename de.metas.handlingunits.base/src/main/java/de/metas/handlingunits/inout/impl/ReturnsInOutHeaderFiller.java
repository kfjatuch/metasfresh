package de.metas.handlingunits.inout.impl;

import java.sql.Timestamp;

import org.compiere.model.I_C_Order;
import org.compiere.model.X_C_DocType;

import de.metas.inout.IInOutBL;
import de.metas.util.Services;
import lombok.NonNull;

/*
 * #%L
 * de.metas.handlingunits.base
 * %%
 * Copyright (C) 2017 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

/**
 * Helper class used to fill an empties InOut header.
 * 
 * @author metas-dev <dev@metasfresh.com>
 *
 */
class ReturnsInOutHeaderFiller
{
	public static ReturnsInOutHeaderFiller newInstance()
	{
		return new ReturnsInOutHeaderFiller();
	}

	@FunctionalInterface
	public static interface IReturnsDocTypeIdProvider
	{
		int getReturnsDocTypeId(String docBaseType, boolean isSOTrx, int adClientId, int adOrgId);
	}

	// services
	private final transient IInOutBL inOutBL = Services.get(IInOutBL.class);

	//
	// Parameters
	private String movementType;
	private IReturnsDocTypeIdProvider returnsDocTypeIdProvider;
	private int bpartnerId;
	private int bpartnerLocationId;
	private Timestamp movementDate;
	private int warehouseId;
	private I_C_Order order;

	private ReturnsInOutHeaderFiller()
	{
	}

	public void fill(@NonNull final org.compiere.model.I_M_InOut returnsInOut)
	{
		//
		// Document Type
		{
			final String movementType = getMovementType();
			final boolean isSOTrx = inOutBL.getSOTrxFromMovementType(movementType);
			// isSOTrx = 'Y' means packing material coming back from the customer -> incoming -> Receipt
			// isSOTrx = 'N' means packing material is returned to the vendor -> outgoing -> Delivery
			final String docBaseType = isSOTrx ? X_C_DocType.DOCBASETYPE_MaterialReceipt : X_C_DocType.DOCBASETYPE_MaterialDelivery;

			returnsInOut.setMovementType(movementType);
			returnsInOut.setIsSOTrx(isSOTrx);

			final int docTypeId = getReturnsDocTypeId(docBaseType, isSOTrx, returnsInOut.getAD_Client_ID(), returnsInOut.getAD_Org_ID());

			returnsInOut.setC_DocType_ID(docTypeId);
		}

		//
		// BPartner, Location & Contact
		{
			final int bpartnerId = getBPartnerId();
			if (bpartnerId > 0)
			{
				returnsInOut.setC_BPartner_ID(bpartnerId);

				final int bpartnerLocationId = getBPartnerLocationId();
				if (bpartnerLocationId > 0)
				{
					returnsInOut.setC_BPartner_Location_ID(bpartnerLocationId);
				}

				// inout.setAD_User_ID(bpartnerContactId); // TODO
			}
		}

		//
		// Document Dates
		{
			final Timestamp movementDate = getMovementDate();
			if (movementDate != null)
			{
				returnsInOut.setDateOrdered(movementDate);
				returnsInOut.setMovementDate(movementDate);
				returnsInOut.setDateAcct(movementDate);
			}
		}

		//
		// Warehouse
		{
			final int warehouseId = getWarehouseId();
			if (warehouseId > 0)
			{
				returnsInOut.setM_Warehouse_ID(warehouseId);
			}
		}

		//
		// task #643: Add order related details
		{
			final I_C_Order order = getOrder();

			if (order == null)
			{
				// nothing to do. The order was not selected
			}
			else
			{
				// if the order was selected, set its poreference to the inout
				final String poReference = order.getPOReference();

				returnsInOut.setPOReference(poReference);
				returnsInOut.setC_Order(order);
			}
		}
	}

	public ReturnsInOutHeaderFiller setMovementType(final String movementType)
	{
		this.movementType = movementType;
		return this;
	}

	private String getMovementType()
	{
		return movementType;
	}

	public ReturnsInOutHeaderFiller setReturnsDocTypeIdProvider(final IReturnsDocTypeIdProvider returnsDocTypeIdProvider)
	{
		this.returnsDocTypeIdProvider = returnsDocTypeIdProvider;
		return this;
	}

	private int getReturnsDocTypeId(final String docBaseType, final boolean isSOTrx, final int adClientId, final int adOrgId)
	{
		return returnsDocTypeIdProvider.getReturnsDocTypeId(docBaseType, isSOTrx, adClientId, adOrgId);
	}

	public ReturnsInOutHeaderFiller setBPartnerId(final int bpartnerId)
	{
		this.bpartnerId = bpartnerId;
		return this;
	}

	private int getBPartnerId()
	{
		return bpartnerId;
	}

	public ReturnsInOutHeaderFiller setBPartnerLocationId(final int bpartnerLocationId)
	{
		this.bpartnerLocationId = bpartnerLocationId;
		return this;
	}

	private int getBPartnerLocationId()
	{
		return bpartnerLocationId;
	}

	public ReturnsInOutHeaderFiller setMovementDate(final Timestamp movementDate)
	{
		this.movementDate = movementDate;
		return this;
	}

	private Timestamp getMovementDate()
	{
		return movementDate;
	}

	public ReturnsInOutHeaderFiller setWarehouseId(final int warehouseId)
	{
		this.warehouseId = warehouseId;
		return this;
	}

	private int getWarehouseId()
	{
		return warehouseId;
	}

	public ReturnsInOutHeaderFiller setOrder(final I_C_Order order)
	{
		this.order = order;
		return this;
	}

	private I_C_Order getOrder()
	{
		return order;
	}
}

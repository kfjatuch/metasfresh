package org.adempiere.ad.security.model.validator;

/*
 * #%L
 * de.metas.adempiere.adempiere.base
 * %%
 * Copyright (C) 2015 metas GmbH
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

import java.util.Properties;

import org.adempiere.ad.modelvalidator.annotations.Interceptor;
import org.adempiere.ad.modelvalidator.annotations.ModelChange;
import org.adempiere.ad.security.IRoleDAO;
import org.adempiere.ad.security.IUserRolePermissionsDAO;
import org.adempiere.model.InterfaceWrapperHelper;
import org.compiere.model.I_AD_Workflow;
import org.compiere.model.ModelValidator;

import de.metas.adempiere.model.I_AD_Role;
import de.metas.util.Services;

@Interceptor(I_AD_Workflow.class)
public class AD_Workflow
{
	public static final transient AD_Workflow instance = new AD_Workflow();

	@ModelChange(timings = ModelValidator.TYPE_AFTER_NEW)
	public void addAccessToRolesWithAutomaticMaintenance(final I_AD_Workflow workflow)
	{
		final IUserRolePermissionsDAO permissionsDAO = Services.get(IUserRolePermissionsDAO.class);

		final int adWorkflowId = workflow.getAD_Workflow_ID();
		final Properties ctx = InterfaceWrapperHelper.getCtx(workflow);
		for (final I_AD_Role role : Services.get(IRoleDAO.class).retrieveAllRolesWithAutoMaintenance(ctx))
		{
			final boolean readWrite = true;
			permissionsDAO.createWorkflowAccess(role, adWorkflowId, readWrite);
		}

	}
}

/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 2008 SC ARHIPAC SERVICE SRL. All Rights Reserved.            *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/
package org.compiere.apps;

import java.awt.Cursor;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JFileChooser;

import org.compiere.model.GridTab;
import org.compiere.util.Env;
import org.compiere.util.ExtensionFileFilter;
import org.slf4j.Logger;

import de.metas.i18n.Msg;
import de.metas.impexp.excel.GridTabExcelExporter;
import de.metas.logging.LogManager;
import lombok.NonNull;

/**
 * Export button consequences
 * @author Teo Sarca, www.arhipac.ro
 *  			<li>FR [ 1943731 ] Window data export functionality
 */
public class AExport
{
	private Logger log = LogManager.getLogger(getClass());	
	private Properties m_ctx = null;
	private int m_WindowNo = 0;
	
	public AExport(APanel parent)
	{
		m_ctx = Env.getCtx();
		m_WindowNo = parent.getWindowNo();
		//
		JFileChooser chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new ExtensionFileFilter("xls", Msg.getMsg(m_ctx, "FileXLS")));
		if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION)
			return;

		//	Create File
		File outFile = ExtensionFileFilter.getFile(chooser.getSelectedFile(), chooser.getFileFilter());
		try
		{
			outFile.createNewFile();
		}
		catch (IOException e)
		{
			log.error("", e);
			ADialog.error(m_WindowNo, parent, "FileCannotCreate", e.getLocalizedMessage());
			return;
		}

		String ext = outFile.getPath();
		//	no extension
		if (ext.lastIndexOf('.') == -1)
		{
			ADialog.error(m_WindowNo, parent, "FileInvalidExtension");
			return;
		}
		ext = ext.substring(ext.lastIndexOf('.')+1).toLowerCase();
		log.info( "File=" + outFile.getPath() + "; Type=" + ext);

		parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try
		{
			if (ext.equals("xls"))
			{
				createXLS(outFile, parent.getCurrentTab());
			}
			else
			{
				ADialog.error(m_WindowNo, parent, "FileInvalidExtension");
			}
		}
		catch (Exception e)
		{
			ADialog.error(m_WindowNo, parent, "Error", e.getLocalizedMessage());
			if (LogManager.isLevelFinest())
				e.printStackTrace();
		}
		finally
		{
			parent.setCursor(Cursor.getDefaultCursor());
		}
	}
	
	private void createXLS(@NonNull final File outFile, @NonNull final GridTab tab)
	{
		final GridTabExcelExporter exporter = new GridTabExcelExporter(tab);
		exporter.exportToFile(outFile);
		Env.startBrowser(outFile);
	}
}

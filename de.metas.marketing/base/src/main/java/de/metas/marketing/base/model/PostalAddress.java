package de.metas.marketing.base.model;

import java.util.Optional;

import javax.annotation.Nullable;

import org.adempiere.util.Check;

import lombok.NonNull;
import lombok.Value;

/*
 * #%L
 * marketing-base
 * %%
 * Copyright (C) 2018 metas GmbH
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

@Value
public class PostalAddress implements ContactAddress
{
	public static Optional<PostalAddress> cast(@Nullable final ContactAddress contactAddress)
	{
		if (contactAddress != null && contactAddress instanceof PostalAddress)
		{
			return Optional.of((PostalAddress)contactAddress);
		}
		return Optional.empty();
	}

	public static String getPostalAddessStringOrNull(@Nullable final ContactAddress contactAddress)
	{
		final Optional<String> stringIfPresent = PostalAddress
				.cast(contactAddress)
				.map(PostalAddress::getValue)
				.filter(s -> !Check.isEmpty(s, true));

		return stringIfPresent.orElse(null);
	}

	public static Boolean getActiveOnRemotePlatformOrNull(@Nullable final ContactAddress contactAddress)
	{
		final Optional<Boolean> boolIfPresent = PostalAddress
				.cast(contactAddress)
				.map(PostalAddress::getDeactivatedOnRemotePlatform);

		return boolIfPresent.orElse(null);
	}

	public static PostalAddress of(@NonNull final String postalAddress)
	{
		return new PostalAddress(postalAddress, null);
	}

	public static PostalAddress of(
			@NonNull final String emailAddress,
			final boolean deactivatedOnRemotePlatform)
	{
		return new PostalAddress(emailAddress, deactivatedOnRemotePlatform);
	}

	String value;

	/** null means "unknown" */
	Boolean deactivatedOnRemotePlatform;

	public PostalAddress(
			@NonNull final String value,
			@Nullable final Boolean deactivatedOnRemotePlatform)
	{
		this.value = Check.assumeNotEmpty(value, "The given value may not be empty");
		this.deactivatedOnRemotePlatform = deactivatedOnRemotePlatform;
	}

	@Override
	public TYPE getType()
	{
		return TYPE.POSTAL;
	}
}

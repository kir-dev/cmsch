import { AtSign, Building, Clock, Edit, Facebook, Instagram, Link, Users } from 'lucide-react'
import type { FC, ReactNode } from 'react'
import { Link as RouterLink } from 'react-router'

import { LinkButton } from '@/common-components/LinkButton'
import Markdown from '@/common-components/Markdown'
import { Badge } from '@/components/ui/badge'
import { joinPath, useBrandColor, useColorModeValue } from '@/util/core-functions.util'
import { AbsolutePaths } from '@/util/paths'
import type { Community, Organization } from '@/util/views/organization'

type DataSheetProps = {
  organization: Organization | Community
}

/**
 * Data Sheet for RESORTS and COMMUNITIES. Made to unify design on those pages.
 * @param dataObject Organization type object - resort or community
 * @constructor
 */
export const DataSheet: FC<DataSheetProps> = ({ organization }) => {
  const brandColor = useBrandColor()
  const isDataAvailable = organization.established || organization.email || organization.members || organization.interests
  return (
    <>
      {!organization.hideName && (
        <h2 className="text-3xl font-bold text-center sm:text-left mt-5 mb-5 font-heading">{organization.name}</h2>
      )}
      <div className="flex flex-col-reverse sm:flex-row justify-between items-center mt-2 sm:mt-5 gap-4">
        {isDataAvailable && (
          <div className="flex flex-col items-start self-start sm:self-center space-y-2">
            {'resortName' in organization && (
              <DataField icon={<Building className="h-4 w-4" />} label="Reszort">
                <RouterLink to={joinPath(AbsolutePaths.ORGANIZATION, organization.resortId)} className="underline">
                  {organization.resortName}
                </RouterLink>
              </DataField>
            )}
            {organization.established && (
              <DataField icon={<Clock className="h-4 w-4" />} label="Alapítva">
                <div>{organization.established}</div>
              </DataField>
            )}
            {organization.email && (
              <DataField icon={<AtSign className="h-4 w-4" />} label="E-mail">
                <a href={`mailto:${organization.email}`} className="underline">
                  {organization.email}
                </a>
              </DataField>
            )}
            {organization.members && (
              <DataField icon={<Users className="h-4 w-4" />} label="Létszám">
                <div>{organization.members} fő</div>
              </DataField>
            )}
            {organization.interests && (
              <div className="flex flex-wrap gap-1">
                {organization.interests.map((interest) => (
                  <Badge key={interest} variant="secondary">
                    {interest}
                  </Badge>
                ))}
              </div>
            )}
          </div>
        )}
        <OrgLogo {...organization} />
      </div>
      <div className="mt-5">{organization.descriptionParagraphs && <Markdown text={organization.descriptionParagraphs} />}</div>

      <div className="flex flex-wrap gap-4 mt-10 justify-center md:justify-start">
        {organization.website && (
          <LinkButton href={organization.website} external className="flex items-center gap-2">
            <Link className="h-4 w-4" /> Weboldal
          </LinkButton>
        )}
        {organization.application && (
          <LinkButton href={organization.application} external style={{ backgroundColor: brandColor }} className="flex items-center gap-2">
            <Edit className="h-4 w-4" /> Jelentkezés
          </LinkButton>
        )}
        {organization.facebook && (
          <LinkButton href={organization.facebook} external className="flex items-center gap-2 bg-[#1877F2] hover:bg-[#1877F2]/90">
            <Facebook className="h-4 w-4" /> Facebook
          </LinkButton>
        )}
        {organization.instagram && (
          <LinkButton href={organization.instagram} external className="flex items-center gap-2 bg-[#E4405F] hover:bg-[#E4405F]/90">
            <Instagram className="h-4 w-4" /> Instagram
          </LinkButton>
        )}
      </div>
    </>
  )
}

type DataFieldProps = {
  children?: ReactNode
  icon: ReactNode
  label: string
}

const DataField: FC<DataFieldProps> = ({ icon, label, children }) => (
  <div className="flex flex-row items-center space-x-2 text-muted-foreground">
    <div>{icon}</div>
    <div className="font-bold">{label}</div>
    {children}
  </div>
)

const OrgLogo = ({ logo, darkLogo, name }: Organization | Community) => {
  let logoSource: string | null | undefined
  const conditionalLogo = useColorModeValue(logo, darkLogo)
  if (logo) {
    logoSource = darkLogo ? conditionalLogo : logo
  } else {
    logoSource = darkLogo
  }

  if (logoSource) {
    return (
      <img
        className={
          'my-2 self-center sm:self-start max-h-40 sm:max-h-40 md:max-h-48 ' +
          'max-w-[16rem] sm:max-w-[10rem] md:max-w-[16rem] object-contain'
        }
        src={logoSource}
        alt={name}
      />
    )
  }

  return null
}

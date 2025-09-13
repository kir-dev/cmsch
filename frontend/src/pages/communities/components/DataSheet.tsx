import { EditIcon, LinkIcon } from '@chakra-ui/icons'
import { Box, Flex, Heading, HStack, Image, Link, Tag, useColorModeValue, VStack, Wrap } from '@chakra-ui/react'
import { FC, ReactNode } from 'react'
import { FaAt, FaBuilding, FaBusinessTime, FaFacebook, FaInstagram, FaUsers } from 'react-icons/fa'

import { LinkButton } from '../../../common-components/LinkButton'
import Markdown from '../../../common-components/Markdown'
import { joinPath, useBrandColor } from '../../../util/core-functions.util'
import { AbsolutePaths } from '../../../util/paths'
import { Community, Organization } from '../../../util/views/organization'

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
      {!organization.hideName && <Heading textAlign={{ base: 'center', sm: 'left' }}>{organization.name}</Heading>}
      <Flex flexDir={{ base: 'column-reverse', sm: 'row' }} justify="space-between" align="center" mt={{ base: 2, sm: 5 }}>
        {isDataAvailable && (
          <VStack alignItems="flex-start" alignSelf={{ base: 'flex-start', sm: 'center' }}>
            {'resortName' in organization && (
              <DataField icon={<FaBuilding />} label="Reszort">
                <Link href={joinPath(AbsolutePaths.ORGANIZATION, organization.resortId)}>{organization.resortName}</Link>
              </DataField>
            )}
            {organization.established && (
              <DataField icon={<FaBusinessTime />} label="Alapítva">
                <Box>{organization.established}</Box>
              </DataField>
            )}
            {organization.email && (
              <DataField icon={<FaAt />} label="E-mail">
                <Link href={`mailto:${organization.email}`}>{organization.email}</Link>
              </DataField>
            )}
            {organization.members && (
              <DataField icon={<FaUsers />} label="Létszám">
                <Box>{organization.members} fő</Box>
              </DataField>
            )}
            {organization.interests && (
              <Flex flexWrap="wrap">
                {organization.interests.map((interest) => (
                  <Box key={interest} p={0.5}>
                    <Tag colorScheme={organization.color} variant="solid">
                      {interest}
                    </Tag>
                  </Box>
                ))}
              </Flex>
            )}
          </VStack>
        )}
        <OrgLogo {...organization} />
      </Flex>
      {organization.descriptionParagraphs && <Markdown text={organization.descriptionParagraphs} />}

      <Wrap marginTop={10} justify={{ base: 'center', md: 'flex-start' }}>
        {organization.website && (
          <LinkButton href={organization.website} external leftIcon={<LinkIcon />} colorScheme={organization.color}>
            Weboldal
          </LinkButton>
        )}
        {organization.application && (
          <LinkButton href={organization.application} external leftIcon={<EditIcon />} colorScheme={brandColor}>
            Jelentkezés
          </LinkButton>
        )}
        {organization.facebook && (
          <LinkButton href={organization.facebook} external leftIcon={<FaFacebook />} colorScheme="blue">
            Facebook
          </LinkButton>
        )}
        {organization.instagram && (
          <LinkButton href={organization.instagram} external leftIcon={<FaInstagram />} colorScheme="purple">
            Instagram
          </LinkButton>
        )}
      </Wrap>
    </>
  )
}

type DataFieldProps = {
  children?: ReactNode
  icon: ReactNode
  label: string
}

const DataField: FC<DataFieldProps> = ({ icon, label, children }) => (
  <HStack color={useColorModeValue('gray.700', 'gray.200')}>
    <Box>{icon}</Box>
    <Box fontWeight={700}>{label}</Box>
    {children}
  </HStack>
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
      <Image
        my={2}
        alignSelf={{ base: 'center', sm: 'flex-start' }}
        src={logoSource}
        alt={name}
        maxH={{ base: '10rem', sm: '10rem', md: '12rem' }}
        maxW={{ base: '16rem', sm: '10rem', md: '16rem' }}
        objectFit="contain"
      />
    )
  }

  return null
}

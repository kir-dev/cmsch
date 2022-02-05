import React from 'react'
import { Box, Flex, Heading, HStack, Image, Tag, VStack, Wrap } from '@chakra-ui/react'
import { Paragraph } from './Basics'
import { Organization } from '../../types/Organization'
import { EditIcon, LinkIcon } from '@chakra-ui/icons'
import { LinkButton } from './LinkButton'
import { FaAt, FaBusinessTime, FaFacebook, FaInstagram, FaUsers } from 'react-icons/fa'
import { useColorModeValue } from '@chakra-ui/system'

type DataSheetProps = {
  organization: Organization
}

/**
 * Data Sheet for RESORTS and COMMUNITIES. Made to unify design on those pages.
 * @param dataObject Organization type object - resort or community
 * @constructor
 */
export const DataSheet: React.FC<DataSheetProps> = ({ organization }) => (
  <>
    {!organization.hideName && <Heading>{organization.name}</Heading>}
    <Flex flexDir={{ base: 'column-reverse', sm: 'row' }} justify="space-between" align="center" mt={{ base: 2, sm: 5 }}>
      <VStack alignItems="flex-start" alignSelf={{ base: 'flex-start', sm: 'center' }}>
        {organization.established && (
          <DataField icon={<FaBusinessTime />} label="Alapítva">
            <Box>{organization.established}</Box>
          </DataField>
        )}
        {organization.email && (
          <DataField icon={<FaAt />} label="E-mail">
            <Box>{organization.email.replace('@', '[at]')}</Box>
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
              <Box p={0.5}>
                <Tag colorScheme={organization.color} variant="solid" key={interest}>
                  {interest}
                </Tag>
              </Box>
            ))}
          </Flex>
        )}
      </VStack>
      {generateLogo(organization)}
    </Flex>
    {organization.descriptionParagraphs && generateParagraphs(organization.descriptionParagraphs)}

    <Wrap marginTop={10} justify={{ base: 'center', md: 'flex-end' }}>
      {organization.website && (
        <LinkButton href={organization.website} external leftIcon={<LinkIcon />} colorScheme={organization.color}>
          Weboldal
        </LinkButton>
      )}
      {organization.application && (
        <LinkButton href={organization.application} external leftIcon={<EditIcon />} colorScheme="brand">
          Jelentkezés
        </LinkButton>
      )}
      {organization.facebook && (
        <LinkButton href={organization.facebook} external leftIcon={<FaFacebook />} colorScheme="facebook">
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

type DataFieldProps = {
  icon: JSX.Element
  label: string
}

const DataField: React.FC<DataFieldProps> = ({ icon, label, children }) => {
  return (
    <HStack color={useColorModeValue('gray.700', 'gray.200')}>
      <Box>{icon}</Box>
      <Box fontWeight={700}>{label}</Box>
      {children}
    </HStack>
  )
}

const generateParagraphs = (paragraphs: string | string[]): JSX.Element => {
  if (Array.isArray(paragraphs)) {
    return (
      <>
        {paragraphs.map((paragraph, index) => (
          <Paragraph key={`${index}@${paragraph.substring(0, 16)}`}>{paragraph}</Paragraph>
        ))}
      </>
    )
  }

  return <Paragraph>{paragraphs}</Paragraph>
}

const generateLogo = (org: Organization): JSX.Element | null => {
  let logoSource: string | null | undefined = null

  if (org.logo) {
    logoSource = org.darkLogo ? useColorModeValue(org.logo, org.darkLogo) : org.logo
  } else {
    logoSource = org.darkLogo
  }

  if (logoSource) {
    return (
      <Image
        my={2}
        alignSelf={{ base: 'flex-end', sm: 'flex-start' }}
        src={logoSource}
        alt={org.name}
        maxH={{ base: '10rem', sm: '10rem', md: '16rem' }}
        maxW={{ base: '16rem', sm: '10rem', md: '16rem' }}
        objectFit="contain"
      />
    )
  }

  return null
}

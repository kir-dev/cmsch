import { EditIcon, LinkIcon } from '@chakra-ui/icons'
import { Box, Flex, Heading, HStack, Image, Link, Tag, VStack, Wrap } from '@chakra-ui/react'
import { useColorModeValue } from '@chakra-ui/system'
import { FC, ReactNode } from 'react'
import { FaAt, FaBusinessTime, FaFacebook, FaInstagram, FaUsers } from 'react-icons/fa'
import { LinkButton } from '../../../common-components/LinkButton'
import { Paragraph } from '../../../common-components/Paragraph'
import { Organization } from '../../../util/views/organization'

type DataSheetProps = {
  organization: Organization
}

/**
 * Data Sheet for RESORTS and COMMUNITIES. Made to unify design on those pages.
 * @param dataObject Organization type object - resort or community
 * @constructor
 */
export const DataSheet: FC<DataSheetProps> = ({ organization }) => {
  const isDataAvailable = organization.established || organization.email || organization.members || organization.interests
  return (
    <>
      {!organization.hideName && <Heading textAlign={{ base: 'center', sm: 'left' }}>{organization.name}</Heading>}
      <Flex flexDir={{ base: 'column-reverse', sm: 'row' }} justify="space-between" align="center" mt={{ base: 2, sm: 5 }}>
        {isDataAvailable && (
          <VStack alignItems="flex-start" alignSelf={{ base: 'flex-start', sm: 'center' }}>
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
                {organization.interests.split(',').map((interest) => (
                  <Box p={0.5}>
                    <Tag colorScheme={organization.color} variant="solid" key={interest}>
                      {interest}
                    </Tag>
                  </Box>
                ))}
              </Flex>
            )}
          </VStack>
        )}
        {generateLogo(organization)}
      </Flex>
      {organization.descriptionParagraphs && generateParagraphs(organization.descriptionParagraphs)}

      <Wrap marginTop={10} justify={{ base: 'center', md: 'flex-start' }}>
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
}

type DataFieldProps = {
  children?: ReactNode
  icon: JSX.Element
  label: string
}

const DataField: FC<DataFieldProps> = ({ icon, label, children }) => (
  <HStack color={useColorModeValue('gray.700', 'gray.200')}>
    <Box>{icon}</Box>
    <Box fontWeight={700}>{label}</Box>
    {children}
  </HStack>
)

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
  let logoSource: string | null | undefined

  if (org.logo) {
    logoSource = org.darkLogo ? useColorModeValue(org.logo, org.darkLogo) : org.logo
  } else {
    logoSource = org.darkLogo
  }

  if (logoSource) {
    return (
      <Image
        my={2}
        alignSelf={{ base: 'center', sm: 'flex-start' }}
        src={logoSource}
        alt={org.name}
        maxH={{ base: '10rem', sm: '10rem', md: '12rem' }}
        maxW={{ base: '16rem', sm: '10rem', md: '16rem' }}
        objectFit="contain"
      />
    )
  }

  return null
}

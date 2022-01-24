import React from 'react'
import { Flex, Grid, GridItem, Heading, Image, Tag, Wrap } from '@chakra-ui/react'
import { Paragraph } from './Basics'
import { Organization } from '../../types/Organization'
import { EditIcon, LinkIcon } from '@chakra-ui/icons'
import { LinkButton } from './LinkButton'
import { SimpleLink } from './SimpleLink'
import { FaFacebook, FaInstagram } from 'react-icons/fa'

type DataSheetProps = {
  organization: Organization
}

/**
 * Data Sheet for RESORTS and COMMUNITIES. Made to unify design on those pages.
 * @param dataObject Organization type object - resort or community
 * @constructor
 */
export const DataSheet: React.FC<DataSheetProps> = ({ organization }) => {
  return (
    <>
      <Heading>{organization.name}</Heading>
      <Flex justify="space-between" align="center" flexWrap="wrap-reverse">
        <DataGrid>
          {organization.established && <DataField label="Alapítva">{organization.established}</DataField>}
          {organization.email && (
            <DataField label="E-mail">
              <SimpleLink external href={'mailto:' + organization.email}>
                {organization.email}
              </SimpleLink>
            </DataField>
          )}
          {organization.members && <DataField label="Létszám">{organization.members}</DataField>}
          {organization.website && (
            <DataField label="Weboldal">
              <SimpleLink external href={organization.website} color={organization.color}>
                {organization.website}
              </SimpleLink>
            </DataField>
          )}
        </DataGrid>
        {organization.logo && <Image mt={5} src={organization.logo} alt={organization.name} maxH={28} maxW={40} objectFit="contain" />}
      </Flex>
      {organization.description && <Paragraph>{organization.description}</Paragraph>}
      {organization.interests && (
        <>
          <Heading size="md" marginBottom={0}>
            A kör tevékenységei közé tartozik:
          </Heading>
          <Wrap marginTop={2}>
            {organization.interests.map((interest) => (
              <Tag colorScheme={organization.color} variant="solid">
                {interest}
              </Tag>
            ))}
          </Wrap>
        </>
      )}
      <Wrap marginTop={10} justify={['center', 'center', 'flex-start']}>
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

const DataGrid: React.FC = ({ children }) => {
  return (
    <Grid templateColumns="repeat(2,auto)" mt={5} gap={5} width="fit-content" mr={5}>
      {children}
    </Grid>
  )
}

type DataFieldProps = {
  label: string
}

const DataField: React.FC<DataFieldProps> = ({ label, children }) => {
  return (
    <>
      <GridItem color="gray.500" colStart={1} colEnd={1}>
        {label}:
      </GridItem>
      <GridItem color="gray.500" colStart={2} colEnd={2}>
        {children}
      </GridItem>
    </>
  )
}

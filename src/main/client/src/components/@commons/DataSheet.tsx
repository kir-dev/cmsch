import React, { ReactNode } from 'react'
import { Box, ButtonGroup, Grid, GridItem, Heading, Image, ListItem, UnorderedList, Wrap } from '@chakra-ui/react'
import { Paragraph } from './Basics'
import { Organization } from '../../types/Organization'
import { LinkIcon } from '@chakra-ui/icons'
import { LinkButton } from './LinkButton'

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
      {organization.logo && (
        <Box>
          <Image marginTop={5} src={organization.logo} alt={organization.name} maxH={20} objectFit="contain" />
        </Box>
      )}
      <Heading>{organization.name}</Heading>
      <Wrap justify="space-between" marginTop={5} maxW="100%">
        <DataGrid>
          <DataField label="Alapítva" value={organization.established} />
          <DataField label="E-mail" value={organization.email} />
          <DataField label="Létszám" value={organization.members} />
        </DataGrid>
      </Wrap>
      {organization.description && <Paragraph>{organization.description}</Paragraph>}
      {organization.interests && (
        <>
          <Heading size="md" marginBottom={0}>
            A kör tevékenységei közé tartozik:
          </Heading>
          <UnorderedList marginTop={2}>
            {organization.interests.map((interest) => (
              <ListItem>{interest}</ListItem>
            ))}
          </UnorderedList>
        </>
      )}
      <ButtonGroup marginTop={5}>
        {organization.website && (
          <LinkButton href={organization.website} external leftIcon={<LinkIcon />} colorScheme={organization.color}>
            Weboldal
          </LinkButton>
        )}
      </ButtonGroup>
    </>
  )
}

const DataGrid: React.FC = ({ children }) => {
  return (
    <Grid templateColumns="repeat(2,auto)" gap={5} width="fit-content" marginTop={5}>
      {children}
    </Grid>
  )
}

type DataFieldProps = {
  label: string
  value: string | number | ReactNode
}

const DataField: React.FC<DataFieldProps> = ({ label, value }) => {
  if (!value) return null
  return (
    <>
      <GridItem color="gray.500" colStart={1} colEnd={1}>
        {label}:
      </GridItem>
      <GridItem color="gray.500" colStart={2} colEnd={2}>
        {value}
      </GridItem>
    </>
  )
}

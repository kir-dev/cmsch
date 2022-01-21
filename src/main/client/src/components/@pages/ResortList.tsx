import { Page } from '../@layout/Page'
import { Alert, AlertIcon, Heading } from '@chakra-ui/react'
import React from 'react'
import { RESORTS } from '../../content/resorts'
import { CardListItem } from '../@commons/CardListItem'
import { Helmet } from 'react-helmet'

type ResortListProps = {}

export const ResortList: React.FC<ResortListProps> = () => {
  return (
    <Page>
      <Helmet title="Reszortok" />
      <Heading>Reszortok</Heading>
      <Alert variant="left-accent" status="info" marginTop={5}>
        <AlertIcon />
        Minden kör egy-egy reszort alá tartozik.
      </Alert>
      {RESORTS.map((resort) => (
        <CardListItem data={resort} link={'/reszortok/' + resort.id} />
      ))}
    </Page>
  )
}

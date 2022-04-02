import { Alert, AlertIcon, Heading } from '@chakra-ui/react'
import { FC } from 'react'
import { Helmet } from 'react-helmet'
import { RESORTS } from '../../content/resorts'
import { CardListItem } from '../@commons/CardListItem'
import { Page } from '../@layout/Page'

type ResortListProps = {}

export const ResortList: FC<ResortListProps> = () => (
  <Page>
    <Helmet title="Reszortok" />
    <Heading>Reszortok</Heading>
    <Alert variant="left-accent" status="info" marginTop={5}>
      <AlertIcon />
      Minden kör egy-egy reszort alá tartozik.
    </Alert>
    {RESORTS.map((resort) => (
      <CardListItem key={resort.id} data={resort} link={'/reszortok/' + resort.id} />
    ))}
  </Page>
)

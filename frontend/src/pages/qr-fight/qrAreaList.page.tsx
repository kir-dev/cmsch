import { CmschPage } from '../../common-components/layout/CmschPage'
import { Helmet } from 'react-helmet-async'
import { Heading } from '@chakra-ui/react'
import { mockAreas } from './mockAreas'
import { DataDisplayWrapper } from './components/DataDisplayWrapper'

export default function QrAreaListPage() {
  return (
    <CmschPage>
      <Helmet>QR Harc</Helmet>
      <Heading>QR Harc</Heading>
      {mockAreas.map((a) => (
        <DataDisplayWrapper area={a} key={a.id} />
      ))}
    </CmschPage>
  )
}

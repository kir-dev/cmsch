import { Heading } from '@chakra-ui/react'
import { Page } from 'components/@layout/Page'

export const QRScan: React.FC = (props) => {
  return (
    <Page {...props}>
      <Heading>QR kód scannelése</Heading>
    </Page>
  )
}

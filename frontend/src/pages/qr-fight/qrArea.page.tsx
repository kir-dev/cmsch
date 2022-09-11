import { Helmet } from 'react-helmet-async'
import { Heading, Text, VStack } from '@chakra-ui/react'
import { useParams } from 'react-router-dom'

import { CmschPage } from '../../common-components/layout/CmschPage'
import { mockAreas } from './mockAreas'
import { AreaDataDisplay } from './components/AreaDataDisplay'
import { AreaStatusBadge } from './components/AreaStatusBadge'
import { AbsolutePaths } from '../../util/paths'
import { CustomBreadcrumb } from '../../common-components/CustomBreadcrumb'

export default function QrAreaPage() {
  const { id } = useParams()
  const area = mockAreas.find((a) => a.id === id)
  if (!area) return null
  const breadcrumbItems = [
    {
      title: 'Területek',
      to: AbsolutePaths.QR_FIGHT
    },
    {
      title: area.name
    }
  ]
  return (
    <CmschPage>
      <CustomBreadcrumb items={breadcrumbItems} />
      <Helmet>{area.name}</Helmet>
      <Heading>{area.name}</Heading>
      <VStack align="flex-start">
        <Text>{area.level}. szint</Text>
        <AreaStatusBadge area={area} />
      </VStack>
      <AreaDataDisplay area={area} />
    </CmschPage>
  )
}

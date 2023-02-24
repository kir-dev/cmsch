import { Image } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { Navigate, useParams } from 'react-router-dom'
import { Organization } from '../../util/views/organization'
import { CustomBreadcrumb } from '../../common-components/CustomBreadcrumb'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { DataSheet } from './components/DataSheet'
import { Frame } from './components/Frame'
import { Paragraph } from '../../common-components/Paragraph'
import { AbsolutePaths } from '../../util/paths'

export default function OrganizationPage() {
  const params = useParams()
  const organization = ([] as Organization[]).find((c) => c.id === params.name)
  if (!organization) return <Navigate to={AbsolutePaths.ORGANIZATION} />
  const breadcrumbItems = [
    {
      title: 'Reszortok',
      to: AbsolutePaths.ORGANIZATION
    },
    {
      title: organization.name
    }
  ]
  return (
    <CmschPage>
      <Helmet title={organization.name} />
      <CustomBreadcrumb items={breadcrumbItems} mt={5} />
      <DataSheet organization={organization} />
      {!organization.application && <Paragraph>Jelentkezés személyesen.</Paragraph>}

      {organization.videoIds?.map((id) => (
        <Frame key={id} id={id} />
      ))}
      {organization.imageIds?.map((url) => (
        <Image key={url} marginTop={10} src={url} width="100%" height="auto" alt="Körkép" borderRadius="lg" />
      ))}
    </CmschPage>
  )
}

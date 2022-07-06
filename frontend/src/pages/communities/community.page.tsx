import { Image } from '@chakra-ui/react'
import { Helmet } from 'react-helmet'
import { Navigate, useParams } from 'react-router-dom'
import { Community } from '../../util/views/organization'
import { CustomBreadcrumb } from '../../common-components/CustomBreadcrumb'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { DataSheet } from './components/DataSheet'
import { Frame } from './components/Frame'
import { Paragraph } from '../../common-components/Paragraph'
import { AbsolutePaths } from '../../util/paths'

const CommunityPage = () => {
  const params = useParams()
  const community = ([] as Community[]).find((c) => c.id === params.name)
  if (!community) return <Navigate to={AbsolutePaths.COMMUNITIES} />
  const resort = ([] as Community[]).find((r) => r.id === community.resortId)
  const breadcrumbItems = [
    {
      title: 'Reszortok',
      to: '/reszortok'
    },
    {
      title: resort?.name,
      to: `/reszortok/${resort?.id}`,
      color: resort?.color
    },
    {
      title: community.name
    }
  ]
  return (
    <CmschPage>
      <Helmet title={community.name} />
      <CustomBreadcrumb items={breadcrumbItems} mt={5} />
      <DataSheet organization={community} />
      {!community.application && <Paragraph>Jelentkezés személyesen.</Paragraph>}

      {community.videoIds?.map((id) => (
        <Frame key={id} id={id} />
      ))}
      {community.imageIds?.map((url) => (
        <Image key={url} marginTop={10} src={url} width="100%" height="auto" alt="Körkép" borderRadius="lg" />
      ))}
    </CmschPage>
  )
}

export default CommunityPage

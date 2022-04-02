import { Image } from '@chakra-ui/react'
import { FC } from 'react'
import { Helmet } from 'react-helmet'
import { Navigate, useParams } from 'react-router-dom'
import { COMMUNITIES } from '../../content/communities'
import { RESORTS } from '../../content/resorts'
import { CustomBreadcrumb } from '../@commons/CustomBreadcrumb'
import { DataSheet } from '../@commons/DataSheet'
import { Frame } from '../@commons/Frame'
import { Paragraph } from '../@commons/Paragraph'
import { Page } from '../@layout/Page'

type CommunityPageProps = {}

export const CommunityPage: FC<CommunityPageProps> = () => {
  const params = useParams()
  const community = COMMUNITIES.find((c) => c.id === params.name)
  if (!community) return <Navigate to="/korok" />
  const resort = RESORTS.find((r) => r.id === community.resortId)
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
    <Page>
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
    </Page>
  )
}

import { Page } from '../@layout/Page'
import { Image } from '@chakra-ui/react'
import React from 'react'
import { COMMUNITIES } from '../../content/communities'
import { RESORTS } from '../../content/resorts'
import { Navigate, useParams } from 'react-router-dom'
import { DataSheet } from '../@commons/DataSheet'
import { YouTubeFrame } from '../@commons/YouTubeFrame'
import { CustomBreadcrumb } from '../@commons/CustomBreadcrumb'
import { Helmet } from 'react-helmet'

type CommunityPageProps = {}

export const CommunityPage: React.FC<CommunityPageProps> = () => {
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

      {community.videoIds?.map((id) => (
        <YouTubeFrame key={id} videoId={id} />
      ))}
      {community.images?.map((url) => (
        <Image key={url} marginTop={10} src={url} alt="Körkép" borderRadius="lg" />
      ))}
    </Page>
  )
}

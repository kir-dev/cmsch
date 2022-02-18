import { Page } from '../@layout/Page'
import React from 'react'
import { Navigate, useParams } from 'react-router-dom'
import { RESORTS } from '../../content/resorts'
import { COMMUNITIES } from '../../content/communities'
import { CardListItem } from '../@commons/CardListItem'
import { DataSheet } from '../@commons/DataSheet'
import { Helmet } from 'react-helmet'
import { CustomBreadcrumb } from '../@commons/CustomBreadcrumb'
import { Image } from '@chakra-ui/react'

type ResortPageProps = {}

export const ResortPage: React.FC<ResortPageProps> = () => {
  const params = useParams()
  const resort = RESORTS.find((r) => r.id === params.name)
  if (!resort) return <Navigate to="/reszortok" />
  const breadcrumbItems = [
    {
      title: 'Reszortok',
      to: '/reszortok'
    },
    {
      title: resort.name
    }
  ]
  return (
    <Page>
      <Helmet title={resort.name} />
      <CustomBreadcrumb items={breadcrumbItems} />
      <DataSheet organization={resort} />
      {COMMUNITIES.filter((c) => c.resortId === resort.id).map((community) => (
        <CardListItem key={community.id} data={community} link={'/korok/' + community.id} />
      ))}
      {resort.imageIds?.map((url) => (
        <Image key={url} marginTop={10} src={url} width="100%" height="auto" alt="Körkép" borderRadius="lg" />
      ))}
    </Page>
  )
}

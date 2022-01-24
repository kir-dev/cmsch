import { Page } from '../@layout/Page'
import { Image } from '@chakra-ui/react'
import React from 'react'
import { COMMUNITIES } from '../../content/communities'
import { Navigate, useParams } from 'react-router-dom'
import { DataSheet } from '../@commons/DataSheet'

type CommunityPageProps = {}

export const CommunityPage: React.FC<CommunityPageProps> = () => {
  const params = useParams()
  const community = COMMUNITIES.find((c) => c.id === params.name)
  if (!community) return <Navigate to="/korok" />
  return (
    <Page>
      <DataSheet organization={community} />
      {community.images?.map((url) => (
        <Image marginTop={10} src={url} alt="Körkép" borderRadius="lg" />
      ))}
    </Page>
  )
}

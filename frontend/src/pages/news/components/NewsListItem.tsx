import { Box, GridItem, Heading, HStack, Icon, LinkBox, LinkOverlay } from '@chakra-ui/react'
import { FaStar } from 'react-icons/fa'
import { Link } from 'react-router-dom'
import Markdown from '../../../common-components/Markdown'
import { stringifyTimeStamp } from '../../../util/core-functions.util'
import { AbsolutePaths } from '../../../util/paths'
import { NewsArticleView } from '../../../util/views/news.view'

type Props = {
  news: NewsArticleView
  fontSize: string
  useLink?: boolean
}

const NewsListItem = ({ news, fontSize, useLink }: Props) => {
  return (
    <GridItem as={LinkBox} borderRadius="base" borderColor="whiteAlpha.200" borderWidth="1px" p={4}>
      <HStack justifyContent="space-between">
        <Box fontSize="sm" mb={2} fontWeight={300}>
          Közzétéve: {stringifyTimeStamp(news.timestamp)}
        </Box>
        {news.highlighted && (
          <Box>
            <Icon as={FaStar} color="brand.100" w={8} h={8} />
          </Box>
        )}
      </HStack>
      <Heading size={fontSize} my={2}>
        {useLink ? (
          <LinkOverlay as={Link} to={`${AbsolutePaths.NEWS}/${news.url}`}>
            {news.title}
          </LinkOverlay>
        ) : (
          news.title
        )}
      </Heading>
      {news.briefContent && <Markdown text={news.briefContent} />}
    </GridItem>
  )
}

export default NewsListItem

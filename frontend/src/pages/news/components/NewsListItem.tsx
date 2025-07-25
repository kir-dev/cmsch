import { Box, GridItem, Heading, HStack, Icon, Image, LinkBox, LinkOverlay } from '@chakra-ui/react'
import { FaExclamation } from 'react-icons/fa'
import { Link } from 'react-router'
import Markdown from '../../../common-components/Markdown'
import { stringifyTimeStamp } from '../../../util/core-functions.util'
import { AbsolutePaths } from '../../../util/paths'
import { NewsArticleView } from '../../../util/views/news.view'

type Props = {
  news: NewsArticleView
  fontSize: string
  useLink?: boolean
}

export const NewsListItem = ({ news, fontSize, useLink }: Props) => {
  return (
    <GridItem as={LinkBox} borderRadius="base" borderColor={news.highlighted ? 'brand.200' : 'whiteAlpha.200'} borderWidth="1px" p={4}>
      <Box display="flex" flexDirection={['column-reverse', 'column-reverse', 'row']} gap={4}>
        {news.imageUrl && <Image borderRadius="md" w={32} h={32} objectFit="cover" objectPosition="center" src={news.imageUrl} />}
        <Box w="full">
          <HStack justifyContent="space-between">
            {!!news.timestamp && (
              <Box fontSize="sm" mb={2} fontWeight={300}>
                Közzétéve: {stringifyTimeStamp(news.timestamp)}
              </Box>
            )}
            {news.highlighted && (
              <Box>
                <Icon as={FaExclamation} color="brand.200" w={8} h={8} />
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
        </Box>
      </Box>
      {news.briefContent && (
        <Box mt={3}>
          <Markdown text={news.briefContent} />
        </Box>
      )}
    </GridItem>
  )
}

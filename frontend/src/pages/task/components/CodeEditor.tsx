import Editor from 'react-simple-code-editor'
import { useState } from 'react'
import { highlight, languages } from 'prismjs'
import 'prismjs/components/prism-clike'
import 'prismjs/components/prism-javascript'
import 'prismjs/components/prism-python'
import 'prismjs/themes/prism.css'
import { Box } from '@chakra-ui/react'

const CodeEditor = () => {
  const [code, setCode] = useState(`function add(a, b) {\n  return a + b;\n}`)
  return (
    <Box my={5}>
      <Editor
        value={code}
        onValueChange={(code) => setCode(code)}
        highlight={(code) => highlight(code, languages.javascript, 'javascript')}
        padding={10}
        style={{
          fontFamily: '"Fira code", "Fira Mono", monospace',
          fontSize: 12
        }}
      />
    </Box>
  )
}

export default CodeEditor

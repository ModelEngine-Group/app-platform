import { createParser } from './parse.ts'

export class EventSourceParserStream extends TransformStream {
  constructor() {
    let parser;
    super({
      start(controller) {
        parser = createParser((event) => {
          if (event.type === 'event') {
            controller.enqueue(event)
          }
        })
      },
      transform(chunk) {
        parser.feed(chunk)
      },
    })
  }
}
